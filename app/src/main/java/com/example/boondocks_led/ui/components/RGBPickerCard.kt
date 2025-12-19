package com.example.boondocks_led.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.rememberColorPickerController

@Composable
fun RGBPickerCard(
    modifier: Modifier = Modifier,
    title: String = "RGB",
    isOn: Boolean,
    brightness: Float,
    onToggleChanged: (Boolean) -> Unit,
    onBrightnessChanged: (Float) -> Unit,
    onBrightnessChangeFinished: () -> Unit,
    onColorSelected: (Int, Int, Int) -> Unit
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

    Card(
        modifier = modifier
            // This modifier chain achieves the full-screen width with padding
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp) // Inner padding for the card's content
        ) {
            HsvColorPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .padding(10.dp),
                controller = colorController,
                onColorChanged = { colorEnvelope ->
                    if (ignoreInitial) return@HsvColorPicker
                    val color: Color = colorEnvelope.color
                    val red = (color.red * 255).toInt()
                    val green = (color.green * 255).toInt()
                    val blue = (color.blue * 255).toInt()
                    onColorSelected(red, green, blue)

                }
            )

            // Column to arrange the top row and the slider vertically
            Column() {
                // Row for the Text and Switch, aligned vertically
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween // Pushes Text left and Switch right
                ) {
                    Text(text = title)
                    Switch(
                        checked = isOn,
                        onCheckedChange = onToggleChanged
                    )
                }

                // The slider underneath the top row
                Slider(
                    value = brightness,
                    onValueChange = onBrightnessChanged,
                    onValueChangeFinished = onBrightnessChangeFinished,
                    modifier = Modifier.fillMaxWidth() // Spans the full width of the Column
                )


            }
        }
    }
}