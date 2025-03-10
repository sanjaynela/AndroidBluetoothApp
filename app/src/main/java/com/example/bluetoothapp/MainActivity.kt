package com.example.bluetoothapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothapp.ui.theme.BluetoothAppTheme
import com.example.bluetoothapp.ui.screens.DevicesScreen
import com.example.bluetoothapp.ui.screens.ServicesScreen
import com.example.bluetoothapp.ui.screens.LogsScreen

private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        
        // Configure window to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        Log.d(TAG, "onCreate called")
        setContent {
            BluetoothAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val title: String) {
    object Devices : Screen("devices", Icons.Default.Home, "Devices")
    object Services : Screen("services", Icons.Default.Settings, "Services")
    object Logs : Screen("logs", Icons.Default.List, "Logs")
}

@Composable
fun MainScreen() {
    Log.d(TAG, "MainScreen composition started")
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(Screen.Devices.route) }
    val items = remember { listOf(Screen.Devices, Screen.Services, Screen.Logs) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            Log.d(TAG, "Navigation item clicked: ${screen.route}")
                            if (currentRoute != screen.route) {
                                currentRoute = screen.route
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId)
                                    launchSingleTop = true
                                }
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Devices.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Devices.route) { 
                Log.d(TAG, "Composing Devices screen")
                DevicesScreen() 
            }
            composable(Screen.Services.route) { 
                Log.d(TAG, "Composing Services screen")
                ServicesScreen() 
            }
            composable(Screen.Logs.route) { 
                Log.d(TAG, "Composing Logs screen")
                LogsScreen() 
            }
        }
    }
    Log.d(TAG, "MainScreen composition completed")
}