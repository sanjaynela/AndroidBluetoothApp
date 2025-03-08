package com.example.bluetoothapp.ui.screens

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.bluetoothapp.BluetoothManager
import androidx.compose.ui.platform.LocalContext

@Composable
fun DashboardScreen() {
    val context = LocalContext.current
    val bluetoothManager = remember { BluetoothManager.getInstance() }
    val connectedDevice by bluetoothManager.connectedDevice
    val services = bluetoothManager.services

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with connected device info
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = "Connected Device",
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (connectedDevice != null) {
                    Text(
                        text = try {
                            connectedDevice?.name ?: "Unknown Device"
                        } catch (e: SecurityException) {
                            "Unknown Device"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = connectedDevice?.address ?: "",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "No device connected",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        // Services and characteristics list
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(services) { service ->
                ServiceCard(service = service, bluetoothManager = bluetoothManager)
            }
        }
    }
}

@Composable
fun ServiceCard(
    service: BluetoothGattService,
    bluetoothManager: BluetoothManager
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Service",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = service.uuid.toString(),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Collapse" else "Expand"
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                service.characteristics.forEach { characteristic ->
                    CharacteristicItem(characteristic = characteristic, bluetoothManager = bluetoothManager)
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
fun CharacteristicItem(
    characteristic: BluetoothGattCharacteristic,
    bluetoothManager: BluetoothManager
) {
    // Get the current context
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp)
    ) {
        Text(
            text = "Characteristic",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = characteristic.uuid.toString(),
            style = MaterialTheme.typography.bodySmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        
        // Properties
        val properties = mutableListOf<String>()
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
            properties.add("Read")
        }
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0) {
            properties.add("Write")
        }
        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            properties.add("Notify")
        }
        
        Text(
            text = "Properties: ${properties.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall
        )

        if ((characteristic.properties and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
            Button(
                onClick = { bluetoothManager.readCharacteristic(context, characteristic) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Read Value")
            }
        }
    }
} 