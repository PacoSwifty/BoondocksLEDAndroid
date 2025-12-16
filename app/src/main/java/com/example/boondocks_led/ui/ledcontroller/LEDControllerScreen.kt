package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.ui.components.LightControlCard
import com.example.boondocks_led.ui.theme.BoondocksTheme
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun LEDControllerScreen(
    controllerId: String,
    ledViewModel: LEDControllerViewModel = hiltViewModel()
) {

    LaunchedEffect(controllerId) {
        Log.i(TAG, "Calling init from the controllerScreen")
        ledViewModel.init(controllerId)
    }

    val state = ledViewModel.uiState.collectAsState().value
    if (state == null) {
        Log.i(TAG, "State was null in the Screen, returning.")
        return
    }

    LEDScreenContent(
        controllerName = state.name,
        isLightOn = state.isLightOn,
        brightness = state.brightnessSliderValue,
        onColorSelected = ledViewModel::onColorSelected,
        onSliderChanged = ledViewModel::onSliderChanged,
        onToggleChanged = ledViewModel::onToggleChanged
    )

}

@Composable
fun LEDScreenContent(
    modifier: Modifier = Modifier,
    controllerName: String,
    isLightOn: Boolean,
    brightness: Float,
    onColorSelected: (Int, Int, Int) -> Unit,
    onSliderChanged: (Float) -> Unit,
    onToggleChanged: (Boolean) -> Unit
) {

    val colorController = rememberColorPickerController()
    var configured by remember(colorController) { mutableStateOf(false) }

    SideEffect {
        if (!configured) {
            colorController.setWheelRadius(14.dp)
            colorController.setWheelAlpha(1f)
            colorController.setDebounceDuration(200L)
            configured = true
        }
    }


    var ignoreInitial by remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        // Wait until first frame so initial setup emissions are ignored
        withFrameNanos { }
        ignoreInitial = false
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = controllerName,
            modifier = Modifier.padding(vertical = 24.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .padding(10.dp),
            controller = colorController,
            onColorChanged = { colorEnvelope ->
                if (ignoreInitial) return@HsvColorPicker
                val color: Color = colorEnvelope.color
                val red = (color.red * 255).toInt()
                val green = (color.green * 255).toInt()
                val blue = (color.blue * 255).toInt()
                onColorSelected(red,green,blue)

            }
        )

        //todo what goes here? Channel name?
        LightControlCard(
            "Channel Name Placeholder",
            isLightOn,
            onToggleChange = onToggleChanged,
            sliderValue = brightness,
            onSliderChange = onSliderChanged,
            modifier)


    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LightContentPreview() {
    BoondocksTheme {
        LEDScreenContent(
            controllerName = "Controller 1",
            onColorSelected = {r,g,b ->},
            onSliderChanged = {},
            onToggleChanged = {},
            isLightOn = true,
            brightness = 0.5f

        )
    }
}