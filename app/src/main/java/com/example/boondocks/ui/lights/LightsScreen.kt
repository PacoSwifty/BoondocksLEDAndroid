package com.example.boondocks.ui.lights

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.boondocks.R
import com.example.boondocks.ui.theme.BoondocksTheme

@Composable
fun LightsScreen(lightsViewModel: LightsViewModel = hiltViewModel()) {
    val lightsUiState by lightsViewModel.uiState.collectAsStateWithLifecycle()

    LightsContent(
        scene1OnClick = { lightsViewModel.onScene1Clicked() },
        scene2OnClick = { lightsViewModel.onScene2Clicked() },
        scene3OnClick = { lightsViewModel.onScene3Clicked() }
    )

}

@Composable
fun LightsContent(
    scene1OnClick: () -> Unit,
    scene2OnClick: () -> Unit,
    scene3OnClick: () -> Unit) {


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
            modifier = Modifier)

        Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_large)))

        LightPicker(modifier = Modifier)

    }
}

@Composable
fun LightPicker(modifier: Modifier = Modifier) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .size(width = 200.dp, height = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {


        }
    }
}


@Composable
fun ScenePicker(
    onScene1Clicked: () -> Unit,
    onScene2Clicked: () -> Unit,
    onScene3Clicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation))
    ) {
        Column(
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
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

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LightContentPreview() {
    BoondocksTheme {
        LightsContent({}, {}, {})
    }
}