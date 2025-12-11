package com.example.boondocks_led.ui.lights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.boondocks_led.R
import com.example.boondocks_led.ui.lights.scenePicker.ScenePicker
import com.example.boondocks_led.ui.lights.toggleLights.LightList
import com.example.boondocks_led.ui.lights.toggleLights.ToggleLightCard
import com.example.boondocks_led.ui.lights.toggleLights.ToggleLightStateHolder
import com.example.boondocks_led.ui.theme.BoondocksTheme

@Composable
fun LightsScreen(lightsViewModel: LightsViewModel = hiltViewModel()) {
    val lightsUiState by lightsViewModel.uiState.collectAsStateWithLifecycle()

    LightsContent(
        scene1OnClick = { lightsViewModel.onScene1Clicked() },
        scene2OnClick = { lightsViewModel.onScene2Clicked() },
        scene3OnClick = { lightsViewModel.onScene3Clicked() },
        toggleLightOnClick = { lightsViewModel.onToggleLightClicked(it) },
        lightsUiState
    )
}

@Composable
fun LightsContent(
    scene1OnClick: () -> Unit,
    scene2OnClick: () -> Unit,
    scene3OnClick: () -> Unit,
    toggleLightOnClick: (lightId: LightList) -> Unit,
    uiState: LightsUiState = LightsUiState(),
) {
    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(dimensionResource(R.dimen.padding_medium)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScenePicker(
            { scene1OnClick() },
            { scene2OnClick() },
            { scene3OnClick() },
            modifier = Modifier
        )

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))

        ToggleLightCard(
            onToggleLightClicked = { toggleLightOnClick(it) },
            ToggleLightStateHolder(uiState),
            modifier = Modifier
        )
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LightContentPreview() {
    BoondocksTheme {
        LightsContent({}, {}, {}, {})
    }
}