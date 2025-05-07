package com.example.boondocks.ui.lights.scenePicker

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.boondocks.R

/**
 * For the three buttons Scene I, Scene II, and Scene III
 */
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