package com.example.boondocks_led.ui.ledcontroller

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.boondocks_led.data.ControllerType
import com.example.boondocks_led.ui.theme.BoondocksTheme

private fun ControllerType.displayName(): String = when (this) {
    ControllerType.RGBW -> "RGBW"
    ControllerType.RGBPLUS1 -> "RGB+1"
    ControllerType.FOURCHANNEL -> "4Channel"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LEDControllerConfigurationScreen(
    viewModel: LEDControllerConfigViewModel,
    onCancel: () -> Unit,
    onSaveComplete: () -> Unit
) {
    val configState by viewModel.configState.collectAsState()

    LEDControllerConfigurationContent(
        configState = configState,
        onControllerSelected = viewModel::onControllerSelected,
        onTypeSelected = viewModel::onTypeSelected,
        onControllerNameChanged = viewModel::onControllerNameChanged,
        onChannelNameChanged = viewModel::onChannelNameChanged,
        onSave = {
            viewModel.onSave()
            onSaveComplete()
        },
        onCancel = onCancel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LEDControllerConfigurationContent(
    configState: LEDControllerConfigState,
    onControllerSelected: (Int) -> Unit,
    onTypeSelected: (ControllerType) -> Unit,
    onControllerNameChanged: (String) -> Unit,
    onChannelNameChanged: (Int, String) -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    var controllerDropdownExpanded by remember { mutableStateOf(false) }
    var typeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Configure Controller",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Dropdown for controller selection
        ExposedDropdownMenuBox(
            expanded = controllerDropdownExpanded,
            onExpandedChange = { controllerDropdownExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = configState.controllerOptions[configState.selectedControllerIndex],
                onValueChange = {},
                readOnly = true,
                label = { Text("Select Controller") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = controllerDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = controllerDropdownExpanded,
                onDismissRequest = { controllerDropdownExpanded = false }
            ) {
                configState.controllerOptions.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        text = { Text(option) },
                        onClick = {
                            onControllerSelected(index)
                            controllerDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Dropdown for controller type selection
        ExposedDropdownMenuBox(
            expanded = typeDropdownExpanded,
            onExpandedChange = { typeDropdownExpanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = configState.selectedType.displayName(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Controller Type") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeDropdownExpanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = typeDropdownExpanded,
                onDismissRequest = { typeDropdownExpanded = false }
            ) {
                configState.typeOptions.forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type.displayName()) },
                        onClick = {
                            onTypeSelected(type)
                            typeDropdownExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Controller Name field
        OutlinedTextField(
            value = configState.controllerName,
            onValueChange = onControllerNameChanged,
            label = { Text("Controller Name") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        // Channel name fields based on selected type
        when (configState.selectedType) {
            ControllerType.RGBW -> {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "RGBW channel name will match the controller name",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            ControllerType.RGBPLUS1 -> {
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = configState.channelName1,
                    onValueChange = { onChannelNameChanged(1, it) },
                    label = { Text("RGB Channel Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = configState.channelName2,
                    onValueChange = { onChannelNameChanged(2, it) },
                    label = { Text("+1 Channel Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            ControllerType.FOURCHANNEL -> {
                Spacer(modifier = Modifier.height(16.dp))
                for (i in 1..4) {
                    val value = when (i) {
                        1 -> configState.channelName1
                        2 -> configState.channelName2
                        3 -> configState.channelName3
                        else -> configState.channelName4
                    }
                    OutlinedTextField(
                        value = value,
                        onValueChange = { onChannelNameChanged(i, it) },
                        label = { Text("Channel $i Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                    if (i < 4) Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Text("Cancel")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Save button
        Button(
            onClick = onSave,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            Text("Save")
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun LEDControllerConfigurationScreenPreview() {
    BoondocksTheme {
        LEDControllerConfigurationContent(
            configState = LEDControllerConfigState(
                selectedControllerIndex = 0,
                selectedType = ControllerType.RGBW,
                controllerName = "Living Room"
            ),
            onControllerSelected = {},
            onTypeSelected = {},
            onControllerNameChanged = {},
            onChannelNameChanged = { _, _ -> },
            onSave = {},
            onCancel = {}
        )
    }
}
