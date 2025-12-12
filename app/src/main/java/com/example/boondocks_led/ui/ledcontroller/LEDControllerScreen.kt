package com.example.boondocks_led.ui.ledcontroller

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.data.Constants.TAG
import com.example.boondocks_led.ui.theme.BoondocksTheme

@Composable
fun LEDControllerScreen(controllerId: String, ledViewModel: LEDControllerViewModel = hiltViewModel()) {

    LaunchedEffect(controllerId) {
        ledViewModel.init(controllerId)
    }

    val state = ledViewModel.uiState.collectAsState().value
    if (state == null) {
        Log.i(TAG, "State was null in the Screen, returning.")
        return
    }

    LEDScreenContent(
        controllerName = state.name
    )

}

@Composable
fun LEDScreenContent(
    controllerName: String
) {
    Log.i(TAG, controllerName)
    Column(
        modifier = Modifier
            .safeDrawingPadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("This is a $controllerName!")
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LightContentPreview() {
    BoondocksTheme {
        LEDScreenContent("Controller 1")
    }
}