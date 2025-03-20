package com.example.bluetoothapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.bluetoothapp.BluetoothManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : ViewModel() {
    
    val connectedDevice = bluetoothManager.connectedDevice
    val logs = bluetoothManager.logs

    fun disconnect() {
        bluetoothManager.disconnect()
    }
} 