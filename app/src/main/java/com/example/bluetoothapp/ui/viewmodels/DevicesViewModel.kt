package com.example.bluetoothapp.ui.viewmodels

import android.bluetooth.le.ScanResult
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bluetoothapp.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    
    val isScanning = bluetoothManager.isScanning
    val devices = bluetoothManager.devices

    fun initialize(context: Context) {
        bluetoothManager.initialize(context)
    }

    fun startScan(context: Context) {
        bluetoothManager.startScan(context)
    }

    fun stopScan() {
        bluetoothManager.stopScan()
    }

    fun connectToDevice(context: Context, device: android.bluetooth.BluetoothDevice) {
        bluetoothManager.connectToDevice(context, device)
    }
} 