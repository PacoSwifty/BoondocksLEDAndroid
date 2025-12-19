package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.data.ControllerType
import com.example.boondocks_led.ui.components.LightControlCard
import com.example.boondocks_led.ui.components.RGBPickerCard
import com.example.boondocks_led.ui.theme.BoondocksTheme

@Composable
fun LEDControllerScreen(
    controllerId: String,
    type: ControllerType,
    ledViewModel: LEDControllerViewModel = hiltViewModel()
) {

    LaunchedEffect(controllerId) {
        Log.i(TAG, "Calling init from the controllerScreen")
        ledViewModel.init(controllerId, type)
    }

    val state = ledViewModel.uiState.collectAsState().value
    if (state == null) {
        Log.i(TAG, "State was null in the Screen, returning.")
        return
    }

    LEDScreenContent(
        state = state,
        actions = LedActions(
            onColorSelected = ledViewModel::onColorSelected,
            onToggle = ledViewModel::onToggleChanged,
            onBrightness = ledViewModel::onBrightnessChanged
        ),
        onAllOffClicked = ledViewModel::onAllOffClicked
    )
}

@Composable
fun LEDScreenContent(
    state: LEDControllerState,
    actions: LedActions,
    onAllOffClicked: () -> Unit
) {

    val channels: List<ChannelUi> = when (state.type) {
        ControllerType.RGBW -> listOf(
            ChannelUi(LEDChannel.RGB, "RGB", state.isRGBWOn, state.rgbwBrightness),
        )

        ControllerType.RGBPLUS1 -> listOf(
            ChannelUi(LEDChannel.RGB, "RGB", state.isRGBWOn, state.rgbwBrightness),
            ChannelUi(LEDChannel.PLUS_ONE, "+1", state.isPlusOneOn, state.plusOneBrightness),
        )

        ControllerType.FOURCHANNEL -> listOf(
            ChannelUi(
                LEDChannel.CH1,
                "Channel 1",
                state.isFourChanOneOn,
                state.fourChanOneBrightness
            ),
            ChannelUi(
                LEDChannel.CH2,
                "Channel 2",
                state.isFourChanTwoOn,
                state.fourChanTwoBrightness
            ),
            ChannelUi(
                LEDChannel.CH3,
                "Channel 3",
                state.isFourChanThreeOn,
                state.fourChanThreeBrightness
            ),
            ChannelUi(
                LEDChannel.CH4,
                "Channel 4",
                state.isFourChanFourOn,
                state.fourChanFourBrightness
            ),
        )
    }


    Column(
        modifier = Modifier
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = state.name,
            modifier = Modifier.padding(vertical = 24.dp),
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        if (state.type == ControllerType.RGBW || state.type == ControllerType.RGBPLUS1) {
            val rgb = channels.first { it.channel == LEDChannel.RGB }

            RGBPickerCard(
                modifier = Modifier,
                title = rgb.label,
                isOn = rgb.isOn,
                brightness = rgb.brightness,
                onToggleChanged = { enabled -> actions.onToggle(LEDChannel.RGB, enabled) },
                onBrightnessChanged = { v -> actions.onBrightness(LEDChannel.RGB, v) },
                onColorSelected = actions.onColorSelected
            )
        }

        channels
            .filter { it.channel != LEDChannel.RGB }
            .forEach { ch ->
                LightControlCard(
                    ch.label,
                    ch.isOn,
                    onToggleChange = { enabled -> actions.onToggle(ch.channel, enabled) },
                    sliderValue = ch.brightness,
                    onSliderChange = { v -> actions.onBrightness(ch.channel, v) },
                    modifier = Modifier.padding(top = 18.dp)
                )
            }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = onAllOffClicked,
            modifier = Modifier
                .fillMaxWidth()           // Fill the width of the screen
                .padding(24.dp),          // Apply 24dp padding all around
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B0000), // Dark Red background
                contentColor = Color.White          // White text color
            )
        ) {
            Text(text = "All Off")
        }
    }
}


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LightContentPreview() {
    BoondocksTheme {
        LEDScreenContent(
            state = previewState,
            actions = previewLedActions,
            onAllOffClicked = {}

        )
    }
}

data class ChannelUi(
    val channel: LEDChannel,
    val label: String,
    val isOn: Boolean,
    val brightness: Float
)