package com.example.boondocks_led.ui.lights.toggleLights

import com.example.boondocks_led.ui.lights.OLDLightsUiState

class ToggleLightStateHolder(uiState: OLDLightsUiState) {
    val frontDriverEnabled = uiState.frontDriverEnabled
    val backDriveEnabled = uiState.backDriveEnabled
    val rearDriverEnabled = uiState.rearDriverEnabled
    val frontPassengerEnabled = uiState.frontPassengerEnabled
    val backPassengerEnabled = uiState.backPassengerEnabled
    val rearPassengerEnabled = uiState.rearPassengerEnabled
    val lightBarEnabled = uiState.lightBarEnabled
}