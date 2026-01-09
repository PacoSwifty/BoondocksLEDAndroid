package com.example.boondocks_led.ui.navigation

import com.example.boondocks_led.R


interface  BoondocksDestination {
    val icon: Int
    val route: String
}

const val controllerRouteIds = "controller/{controllerId}"

object SceneDestination: BoondocksDestination {
    override val icon = R.drawable.baseline_movie_24
    override val route = "Scenes"
}

object Controller1: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "Controller 1"
}

object Controller2: BoondocksDestination {
    override val icon = R.drawable.baseline_electric_bolt_24
    override val route = "Controller 2"
}

object Controller3: BoondocksDestination {
    override val icon = R.drawable.baseline_water_drop_24
    override val route = "Controller 3"
}

object Controller4: BoondocksDestination {
    override val icon = R.drawable.baseline_severe_cold_24
    override val route = "Controller 4"
}

val tabRowScreens = listOf(SceneDestination, Controller1, Controller2, Controller3, Controller4)