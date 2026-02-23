package com.example.boondocks_led.ui.navigation

import com.example.boondocks_led.R


interface BoondocksDestination {
    val icon: Int
    val route: String
    val iconOverlayNumber: Int?
        get() = null
}

const val controllerRouteIds = "controller/{controllerId}"

object SceneDestination: BoondocksDestination {
    override val icon = R.drawable.baseline_movie_24
    override val route = "Scenes"
}

object Controller1: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "Controller 1"
    override val iconOverlayNumber = 1
}

object Controller2: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "Controller 2"
    override val iconOverlayNumber = 2
}

object Controller3: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "Controller 3"
    override val iconOverlayNumber = 3
}

object Controller4: BoondocksDestination {
    override val icon = R.drawable.baseline_lightbulb_24
    override val route = "Controller 4"
    override val iconOverlayNumber = 4
}

val tabRowScreens = listOf(SceneDestination, Controller1, Controller2, Controller3, Controller4)