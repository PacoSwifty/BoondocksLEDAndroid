package com.example.boondocks_led.ui.lights

data class OLDLightsUiState(
    val currentLightScene: String = "",

    val frontDriverEnabled: Boolean = false,
    val backDriveEnabled: Boolean = false,
    val rearDriverEnabled: Boolean = false,

    val frontPassengerEnabled: Boolean = false,
    val backPassengerEnabled: Boolean = false,
    val rearPassengerEnabled: Boolean = false,

    val lightBarEnabled: Boolean = false
)
