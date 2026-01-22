package com.example.boondocks_led.ui.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel

enum class ScenePageScreen {
    Selection,
    Configuration
}

@Composable
fun ScenePage(
    viewModel: SceneViewModel = hiltViewModel()
) {
    var currentScreen by remember { mutableStateOf(ScenePageScreen.Selection) }

    when (currentScreen) {
        ScenePageScreen.Selection -> {
            SceneSelectionScreen(
                viewModel = viewModel,
                onSettingsTapped = {
                    viewModel.resetConfigurationState()
                    currentScreen = ScenePageScreen.Configuration
                }
            )
        }
        ScenePageScreen.Configuration -> {
            SceneConfigurationScreen(
                viewModel = viewModel,
                onCancel = {
                    currentScreen = ScenePageScreen.Selection
                },
                onSaveComplete = {
                    currentScreen = ScenePageScreen.Selection
                }
            )
        }
    }
}
