package com.example.boondocks.ui.lights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.boondocks.R

@Composable
fun LightsScreen(lightsViewModel: LightsViewModel = viewModel()) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    val lightsUiState by lightsViewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .safeDrawingPadding()
            .padding(mediumPadding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ScenePicker(
            { lightsViewModel.onScene1Clicked() },
            { lightsViewModel.onScene2Clicked() },
            { lightsViewModel.onScene3Clicked() },
            modifier = Modifier)

    }
}

@Composable
fun ScenePicker(
    onScene1Clicked: () -> Unit,
    onScene2Clicked: () -> Unit,
    onScene3Clicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mediumPadding = dimensionResource(R.dimen.padding_medium)
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        Column(
            modifier = Modifier.padding(mediumPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onScene1Clicked() }
            ) {
                Text(
                    text = stringResource(R.string.scene1)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onScene2Clicked() }
            ) {
                Text(
                    text = stringResource(R.string.scene2)
                )
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onScene3Clicked() }
            ) {
                Text(
                    text = stringResource(R.string.scene3)
                )
            }
        }
    }
}

@Preview
@Composable
fun LightsScreenPreview() {
    LightsScreen()
}