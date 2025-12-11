package com.example.boondocks_led.ui.lights.toggleLights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import com.example.boondocks_led.R

/**
 * For toggling lights around the van
 */
@Composable
fun ToggleLightCard(
    onToggleLightClicked: (lightId: LightList) -> Unit,
    toggleState: ToggleLightStateHolder,
    modifier: Modifier = Modifier
) {

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = dimensionResource(R.dimen.card_elevation)),
        modifier = modifier
    ) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {

            // Driver Side Lights
            Row(modifier = modifier) {

                //Back Driver
                ToggleLight(
                    LightList.BACK_DRIVER,
                    dimensionResource(R.dimen.van_standard_light_width),
                    dimensionResource(R.dimen.van_light_thickness),
                    toggleState.backDriveEnabled,
                    onToggleLightClicked
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))

                //Front Driver
                ToggleLight(
                    LightList.FRONT_DRIVER,
                    dimensionResource(R.dimen.van_standard_light_width),
                    dimensionResource(R.dimen.van_light_thickness),
                    toggleState.frontDriverEnabled,
                    onToggleLightClicked
                )
            }

            //Rear lights and front light bar
            Row(modifier = modifier) {

                //Rear Lights
                Column(
                    modifier = modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.Start
                ) {

                    //Rear Driver
                    ToggleLight(
                        LightList.REAR_DRIVER,
                        dimensionResource(R.dimen.van_light_thickness),
                        dimensionResource(R.dimen.van_standard_light_width),
                        toggleState.rearDriverEnabled,
                        onToggleLightClicked
                    )

                    Spacer(modifier = modifier.size(30.dp))

                    //Rear Passenger
                    ToggleLight(
                        LightList.REAR_PASSENGER,
                        dimensionResource(R.dimen.van_light_thickness),
                        dimensionResource(R.dimen.van_standard_light_width),
                        toggleState.rearPassengerEnabled,
                        onToggleLightClicked
                    )
                }

                Spacer(modifier = modifier.weight(1f))

                // Light Bar
                ToggleLight(
                    LightList.LIGHT_BAR,
                    dimensionResource(R.dimen.van_light_thickness),
                    dimensionResource(R.dimen.van_lightbar_width),
                    toggleState.lightBarEnabled,
                    onToggleLightClicked
                )
            }

            // Passenger  Side Lights
            Row(modifier = modifier) {

                // Back Passenger
                ToggleLight(
                    LightList.BACK_PASSENGER,
                    dimensionResource(R.dimen.van_standard_light_width),
                    dimensionResource(R.dimen.van_light_thickness),
                    toggleState.backPassengerEnabled,
                    onToggleLightClicked
                )

                Spacer(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)))

                // Front Passenger
                ToggleLight(
                    LightList.FRONT_PASSENGER,
                    dimensionResource(R.dimen.van_standard_light_width),
                    dimensionResource(R.dimen.van_light_thickness),
                    toggleState.frontPassengerEnabled,
                    onToggleLightClicked
                )
            }
        }
    }
}