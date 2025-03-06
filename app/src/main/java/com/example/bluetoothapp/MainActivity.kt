package com.example.bluetoothapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.res.painterResource
import androidx.core.view.WindowCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bluetoothapp.ui.theme.BluetoothAppTheme
import com.example.bluetoothapp.ui.screens.HomeScreen
import com.example.bluetoothapp.ui.screens.DashboardScreen
import com.example.bluetoothapp.ui.screens.NotificationsScreen

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

sealed class Screen(val route: String, val icon: Int, val title: String) {
    object Home : Screen("home", R.drawable.ic_home_black_24dp, "Home")
    object Dashboard : Screen("dashboard", R.drawable.ic_dashboard_black_24dp, "Dashboard")
    object Notifications : Screen("notifications", R.drawable.ic_notifications_black_24dp, "Notifications")

    companion object {
        fun fromRoute(route: String?): Screen {
            return when (route) {
                Home.route -> Home
                Dashboard.route -> Dashboard
                Notifications.route -> Notifications
                else -> Home
            }
        }
    }
}

@Composable
fun MainScreen() {
    Log.d(TAG, "MainScreen composition started")
    val navController = rememberNavController()
    var currentRoute by remember { mutableStateOf(Screen.Home.route) }
    val items = remember { listOf(Screen.Home, Screen.Dashboard, Screen.Notifications) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { 
                            Icon(
                                painter = painterResource(id = screen.icon),
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
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) { 
                Log.d(TAG, "Composing Home screen")
                HomeScreen() 
            }
            composable(Screen.Dashboard.route) { 
                Log.d(TAG, "Composing Dashboard screen")
                DashboardScreen() 
            }
            composable(Screen.Notifications.route) { 
                Log.d(TAG, "Composing Notifications screen")
                NotificationsScreen() 
            }
        }
    }
    Log.d(TAG, "MainScreen composition completed")
}