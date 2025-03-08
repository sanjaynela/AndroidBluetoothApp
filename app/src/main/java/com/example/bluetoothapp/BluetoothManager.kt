package com.example.bluetoothapp

import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import java.util.*

class BluetoothManager private constructor() {
    private val _devices = mutableStateListOf<ScanResult>()
    val devices = _devices

    private val _isScanning = mutableStateOf(false)
    val isScanning = _isScanning

    private val _connectedDevice = mutableStateOf<BluetoothDevice?>(null)
    val connectedDevice = _connectedDevice

    private val _services = mutableStateListOf<BluetoothGattService>()
    val services = _services

    private val _logs = mutableStateListOf<String>()
    val logs = _logs

    private var bluetoothGatt: BluetoothGatt? = null
    private var systemBluetoothManager: android.bluetooth.BluetoothManager? = null
    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothLeScanner: BluetoothLeScanner? = null
    private var scanning = false
    private var applicationContext: Context? = null

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            val device = result.device
            if (!_devices.any { it.device.address == device.address }) {
                _devices.add(result)
                val deviceName = try {
                    device.name ?: "Unknown"
                } catch (e: SecurityException) {
                    "Unknown"
                }
                addLog("Found device: $deviceName (${device.address})")
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> {
                    val deviceName = try {
                        gatt?.device?.name ?: "Unknown"
                    } catch (e: SecurityException) {
                        "Unknown"
                    }
                    addLog("Connected to device: $deviceName")
                    _connectedDevice.value = gatt?.device
                    
                    if (applicationContext?.let { hasRequiredPermissions(it) } == true) {
                        try {
                            gatt?.discoverServices()
                        } catch (e: SecurityException) {
                            addLog("Cannot discover services: Security exception")
                            disconnect()
                        }
                    } else {
                        addLog("Cannot discover services: Missing required permissions")
                        disconnect()
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    addLog("Disconnected from device")
                    _connectedDevice.value = null
                    _services.clear()
                }
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt?.services?.let { servicesList ->
                    _services.clear()
                    _services.addAll(servicesList)
                    addLog("Discovered ${servicesList.size} services")
                }
            }
        }

        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            value: ByteArray,
            status: Int
        ) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                val hexString = value.joinToString(separator = " ") { 
                    String.format("%02X", it) 
                }
                addLog("Read characteristic ${characteristic.uuid}: $hexString")
            }
        }
    }

    fun initialize(context: Context) {
        systemBluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? android.bluetooth.BluetoothManager
        bluetoothAdapter = systemBluetoothManager?.adapter
        bluetoothLeScanner = bluetoothAdapter?.bluetoothLeScanner
        addLog("Bluetooth initialized: ${bluetoothAdapter != null}")
    }

    private fun hasRequiredPermissions(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_ADMIN) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    @SuppressLint("MissingPermission")
    fun startScan(context: Context) {
        if (!hasRequiredPermissions(context)) {
            addLog("Cannot start scan: Missing required permissions")
            return
        }
        if (!scanning && bluetoothLeScanner != null) {
            _devices.clear()
            scanning = true
            _isScanning.value = true
            addLog("Started scanning")
            bluetoothLeScanner?.startScan(scanCallback)
        } else {
            addLog("Cannot start scan: Scanner not available")
        }
    }

    @SuppressLint("MissingPermission")
    fun stopScan() {
        if (scanning && bluetoothLeScanner != null) {
            scanning = false
            _isScanning.value = false
            addLog("Stopped scanning")
            bluetoothLeScanner?.stopScan(scanCallback)
        }
    }

    @SuppressLint("MissingPermission")
    fun connectToDevice(context: Context, device: BluetoothDevice) {
        if (!hasRequiredPermissions(context)) {
            addLog("Cannot connect: Missing required permissions")
            return
        }
        applicationContext = context.applicationContext
        addLog("Connecting to device: ${device.name ?: "Unknown"}")
        bluetoothGatt = device.connectGatt(context, false, gattCallback)
    }

    @SuppressLint("MissingPermission")
    fun disconnect() {
        bluetoothGatt?.disconnect()
        bluetoothGatt = null
    }

    @SuppressLint("MissingPermission")
    fun readCharacteristic(context: Context, characteristic: BluetoothGattCharacteristic) {
        if (!hasRequiredPermissions(context)) {
            addLog("Cannot read characteristic: Missing required permissions")
            return
        }
        bluetoothGatt?.readCharacteristic(characteristic)
    }

    private fun addLog(message: String) {
        Log.d(TAG, message)
        _logs.add("${Date()}: $message")
    }

    fun cleanup() {
        disconnect()
        stopScan()
        bluetoothLeScanner = null
        bluetoothAdapter = null
        systemBluetoothManager = null
    }

    companion object {
        private const val TAG = "BluetoothManager"
        
        @Volatile
        private var instance: BluetoothManager? = null

        fun getInstance(): BluetoothManager {
            return instance ?: synchronized(this) {
                instance ?: BluetoothManager().also { instance = it }
            }
        }
    }
} 