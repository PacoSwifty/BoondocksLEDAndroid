package com.example.boondocks.ui.lights.toggleLights

import com.example.boondocks.ui.lights.LightsUiState

class ToggleLightStateHolder(uiState: LightsUiState) {
    val frontDriverEnabled = uiState.frontDriverEnabled
    val backDriveEnabled = uiState.backDriveEnabled
    val rearDriverEnabled = uiState.rearDriverEnabled
    val frontPassengerEnabled = uiState.frontPassengerEnabled
    val backPassengerEnabled = uiState.backPassengerEnabled
    val rearPassengerEnabled = uiState.rearPassengerEnabled
    val lightBarEnabled = uiState.lightBarEnabled
}