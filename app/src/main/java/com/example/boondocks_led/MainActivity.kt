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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import com.example.boondocks_led.ble.ConnectionState
import com.example.boondocks_led.data.ControllerType
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.ui.components.TabRow
import com.example.boondocks_led.ui.ledcontroller.LEDControllerScreen
import com.example.boondocks_led.ui.navigation.tabRowScreens
import com.example.boondocks_led.ui.scene.SceneScreen
import com.example.boondocks_led.ui.splash.SplashScreen
import com.example.boondocks_led.ui.theme.BoondocksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch



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
        val connectionState by mainActivityViewModel.connectionState.collectAsState()

        BoondocksTheme {
            when (connectionState) {
                is ConnectionState.Connected -> MainContent()
                else -> SplashScreen()
            }
        }
    }

    @Composable
    fun MainContent() {
        val pagerState = rememberPagerState(pageCount = { tabRowScreens.size })
        val coroutineScope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                Column {
                    Box(
                        modifier = Modifier
                            .statusBarsPadding()
                            .background(Color.Yellow)
                    )
                    TabRow(
                        allScreens = tabRowScreens,
                        onTabSelected = { index ->
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        selectedTabIndex = pagerState.currentPage,
                    )
                }
            }
        ) { innerPadding ->
            val controllerConfigs = listOf(
                "1" to ControllerType.RGBW,
                "2" to ControllerType.RGBPLUS1,
                "3" to ControllerType.FOURCHANNEL,
                "4" to ControllerType.RGBW
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                userScrollEnabled = false
            ) { page ->
                when (page) {
                    0 -> SceneScreen()
                    else -> {
                        val controllerIndex = page - 1
                        val (controllerId, controllerType) = controllerConfigs[controllerIndex]
                        LEDControllerScreen(
                            controllerId = controllerId,
                            type = controllerType,
                            ledViewModel = hiltViewModel(key = "controller_$controllerId")
                        )
                    }
                }
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