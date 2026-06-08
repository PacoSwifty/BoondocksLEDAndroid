package com.example.boondocks_led.ui.scene

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.boondocks_led.ui.theme.BoondocksTheme

@Composable
fun SceneSelectionScreen(
    viewModel: SceneViewModel = hiltViewModel(),
    onSettingsTapped: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    SceneSelectionContent(
        state = state,
        onButtonTapped = viewModel::onButtonTapped,
        onSettingsTapped = onSettingsTapped,
        onAllOffClicked = viewModel::onAllOffClicked
    )
}

@Composable
fun SceneSelectionContent(
    state: SceneSelectionState,
    onButtonTapped: (Int) -> Unit,
    onSettingsTapped: () -> Unit,
    onAllOffClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        state.buttons.forEachIndexed { index, buttonState ->
            SceneButton(
                text = buttonState.text,
                isSelected = buttonState.isSelected,
                isEnabled = buttonState.isEnabled,
                onClick = { onButtonTapped(index) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        IconButton(
            onClick = onSettingsTapped,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onAllOffClicked,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF8B0000),
                contentColor = Color.White
            )
        ) {
            Text(text = "All Off")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun SceneSelectionScreenPreview() {
    BoondocksTheme {
        SceneSelectionContent(
            state = SceneSelectionState(
                buttons = listOf(
                    SceneButtonState("Evening", isSelected = true),
                    SceneButtonState("Movie", isSelected = false),
                    SceneButtonState("Party", isSelected = false),
                    SceneButtonState("Relax", isSelected = false)
                ),
                selectedIndex = 0
            ),
            onButtonTapped = {},
            onSettingsTapped = {},
            onAllOffClicked = {}
        )
    }
}

@Composable
private fun SceneButton(
    text: String,
    isSelected: Boolean,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        enabled = isEnabled,
        modifier = modifier.height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            },
            contentColor = if (isSelected) {
                MaterialTheme.colorScheme.onPrimary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    ) {
        Text(text = text)
    }
}
