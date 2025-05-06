package com.example.boondocks.ui.lights

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

enum class LightList {
    FRONT_DRIVER,
    REAR_DRIVER,
    BACK_DRIVER,
    FRONT_PASSENGER,
    REAR_PASSENGER,
    BACK_PASSENGER,
    LIGHT_BAR
}

@Composable
fun ToggleLight(
    lightId: LightList,
    width: Dp,
    height: Dp,
    onLightClicked: (id: LightList) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(
                width = width,
                height = height
            )
            .background(Color.Blue)
            .clickable { onLightClicked(lightId) })
    
}