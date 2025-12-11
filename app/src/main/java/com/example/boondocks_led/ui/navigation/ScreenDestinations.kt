package com.example.boondocks_led.ui.navigation

import com.example.boondocks_led.R


interface BoondocksDestination {
    val icon: Int
    val route: String
}

object Lights: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "lights"
}

object Motors: BoondocksDestination {
    override val icon = R.drawable.baseline_electric_bolt_24
    override val route = "motors"
}

object Water: BoondocksDestination {
    override val icon = R.drawable.baseline_water_drop_24
    override val route = "water"
}

object Fridge: BoondocksDestination {
    override val icon = R.drawable.baseline_severe_cold_24
    override val route = "fridge"
}

val tabRowScreens = listOf(Lights, Motors, Water, Fridge)