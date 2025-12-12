package com.example.boondocks_led.ui.navigation

import com.example.boondocks_led.R


interface  BoondocksDestination {
    val icon: Int
    val route: String
}

const val controllerRouteIds = "controller/{controllerId}"

object Controller1: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "controller/1"
}

object Controller2: BoondocksDestination {
    override val icon = R.drawable.baseline_electric_bolt_24
    override val route = "controller/2"
}

object Controller3: BoondocksDestination {
    override val icon = R.drawable.baseline_water_drop_24
    override val route = "controller/3"
}

object Controller4: BoondocksDestination {
    override val icon = R.drawable.baseline_severe_cold_24
    override val route = "controller/4"
}

val tabRowScreens = listOf(Controller1, Controller2, Controller3, Controller4)