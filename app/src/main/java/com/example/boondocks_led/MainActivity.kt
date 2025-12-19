package com.example.boondocks_led

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.boondocks_led.ui.components.TabRow
import com.example.boondocks_led.ui.navigation.BoondocksNavHost
import com.example.boondocks_led.ui.navigation.Controller1
import com.example.boondocks_led.ui.navigation.navigateToController
import com.example.boondocks_led.ui.navigation.tabRowScreens
import com.example.boondocks_led.ui.theme.BoondocksTheme
import dagger.hilt.android.AndroidEntryPoint



@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //region bluetooth variables

    private val bluetoothPermissions = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val bluetoothRequestCode = 1001

    //endregion


    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoondocksApp()
        }

        requestBluetoothPermissions()

    }

    @Composable
    fun BoondocksApp() {
        val navController = rememberNavController()
        val currentBackStack by navController.currentBackStackEntryAsState()
        val currentDestination = currentBackStack?.destination

        AppScreenContent(navController, currentBackStack, currentDestination)
    }

    @Composable
    fun AppScreenContent(
        navController: NavHostController,
        currentBackStack: NavBackStackEntry?,
        currentDestination: NavDestination?
    ) {
        BoondocksTheme {

            val currentScreen =
                tabRowScreens.find { it.route == currentDestination?.route } ?: Controller1
            Scaffold(
                topBar = {
                    Column() {
                        Box(
                            modifier = Modifier
                                .statusBarsPadding()
                                .background(Color.Yellow)
                        )
                        TabRow(
                            allScreens = tabRowScreens,
                            onTabSelected = { newScreen ->
                                navController.navigateToController(newScreen.route)
                            },
                            currentScreen = currentScreen,
                        )
                    }
                }

            ) { innerPadding ->
                BoondocksNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    private fun requestBluetoothPermissions() {
        if (bluetoothPermissions.all {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }) {
            mainActivityViewModel.startBle()
        } else {
            ActivityCompat.requestPermissions(this, bluetoothPermissions, bluetoothRequestCode)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == bluetoothRequestCode) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                // Permissions granted, proceed with Bluetooth operations
                mainActivityViewModel.startBle()
            } else {
                // todo Permissions denied, handle accordingly (e.g., show a message)
                Toast.makeText(this, "Need Bluetooth Permissions", Toast.LENGTH_LONG).show()
            }
        }
    }
}