package com.example.boondocks_led.ui.scene

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.data.SceneConfig

enum class ScenePageScreen {
    Selection,
    Configuration
}

@Composable
fun ScenePage(
    sceneNames: Map<String, SceneConfig>? = null,
    viewModel: SceneViewModel = hiltViewModel()
) {
    LaunchedEffect(sceneNames) {
        sceneNames?.let { viewModel.applySceneNames(it) }
    }
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
