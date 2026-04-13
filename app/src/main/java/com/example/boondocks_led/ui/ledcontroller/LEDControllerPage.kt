package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.data.ControllerConfig

enum class LEDControllerPageScreen {
    Controller,
    Configuration
}

@Composable
fun LEDControllerPage(
    controllerId: String,
    config: ControllerConfig
) {
    var currentScreen by remember { mutableStateOf(LEDControllerPageScreen.Controller) }
    val configViewModel: LEDControllerConfigViewModel = hiltViewModel(key = "config_$controllerId")
    val ledViewModel: LEDControllerViewModel = hiltViewModel(key = "controller_$controllerId")

    when (currentScreen) {
        LEDControllerPageScreen.Controller -> {
            LEDControllerScreen(
                controllerId = controllerId,
                config = config,
                onSettingsTapped = {
                    Log.i("LEDControllerPage", "Settings tapped for controller $controllerId")
                    configViewModel.initWithController(controllerId, config)
                    currentScreen = LEDControllerPageScreen.Configuration
                },
                ledViewModel = ledViewModel
            )
        }
        LEDControllerPageScreen.Configuration -> {
            LEDControllerConfigurationScreen(
                viewModel = configViewModel,
                onCancel = {
                    currentScreen = LEDControllerPageScreen.Controller
                },
                onSaveComplete = {
                    currentScreen = LEDControllerPageScreen.Controller
                }
            )
        }
    }
}
