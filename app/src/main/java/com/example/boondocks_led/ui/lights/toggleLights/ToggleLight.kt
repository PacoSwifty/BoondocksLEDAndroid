package com.example.boondocks_led.ui.lights.toggleLights

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import com.example.boondocks_led.R

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
    enabled: Boolean,
    onLightClicked: (id: LightList) -> Unit,
    modifier: Modifier = Modifier
) {

    Button(
        onClick = { onLightClicked(lightId) },
//        colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.AlloyOrange)),
        colors = if (enabled) ButtonDefaults.buttonColors(containerColor = colorResource(R.color.AlloyOrange))
                else ButtonDefaults.buttonColors(containerColor = Color.Gray),
        modifier = modifier
            .size(
                width = width,
                height = height
            )

    ) {}
}