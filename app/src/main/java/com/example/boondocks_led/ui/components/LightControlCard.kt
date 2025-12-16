package com.example.boondocks_led.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * A Card that displays a setting with a title, a toggle switch, and a slider.
 *
 * @param title The text to display on the left side of the card.
 * @param isChecked The current state of the toggle switch.
 * @param onToggleChange A callback that is invoked when the user flips the switch.
 * @param sliderValue The current value of the slider (from 0.0f to 1.0f).
 * @param onSliderChange A callback that is invoked when the user moves the slider.
 * @param modifier The modifier to be applied to the Card.
 */
@Composable
fun LightControlCard(
    title: String,
    isChecked: Boolean,
    onToggleChange: (Boolean) -> Unit,
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            // This modifier chain achieves the full-screen width with padding
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        // Column to arrange the top row and the slider vertically
        Column(
            modifier = Modifier.padding(16.dp) // Inner padding for the card's content
        ) {
            // Row for the Text and Switch, aligned vertically
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween // Pushes Text left and Switch right
            ) {
                Text(text = title)
                Switch(
                    checked = isChecked,
                    onCheckedChange = onToggleChange
                )
            }

            // The slider underneath the top row
            Slider(
                value = sliderValue,
                onValueChange = onSliderChange,
                modifier = Modifier.fillMaxWidth() // Spans the full width of the Column
            )
        }
    }
}

/**
 * A preview function to display the SettingsCard in Android Studio.
 * This demonstrates how to use the composable with state management.
 */
@Preview(showBackground = true, backgroundColor = 0xFFF0F0F0)
@Composable
fun LightControlCardPreview() {
    // 'remember' is used to hold state within the preview
    var isToggled by remember { mutableStateOf(true) }
    var sliderPosition by remember { mutableFloatStateOf(0.75f) }

    LightControlCard(
        title = "Brightness",
        isChecked = isToggled,
        onToggleChange = { newState -> isToggled = newState },
        sliderValue = sliderPosition,
        onSliderChange = { newPosition -> sliderPosition = newPosition }
    )
}