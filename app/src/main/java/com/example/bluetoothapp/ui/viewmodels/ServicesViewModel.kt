package com.example.bluetoothapp.ui.viewmodels

import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.bluetoothapp.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ServicesViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    
    val connectedDevice = bluetoothManager.connectedDevice
    val services = bluetoothManager.services

    fun readCharacteristic(context: Context, characteristic: BluetoothGattCharacteristic) {
        bluetoothManager.readCharacteristic(context, characteristic)
    }
} 