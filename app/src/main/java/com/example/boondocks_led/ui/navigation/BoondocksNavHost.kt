package com.example.boondocks_led.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.boondocks_led.data.ControllerType
import com.example.boondocks_led.ui.ledcontroller.LEDControllerScreen

@Composable
fun BoondocksNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Controller1.route,
        modifier = modifier
    ) {

        composable(route = Controller1.route) {
            LEDControllerScreen(controllerId = "1", ControllerType.RGBW)
        }

        composable(route = Controller2.route) {
            LEDControllerScreen(controllerId = "2", ControllerType.RGBPLUS1)
        }

        composable(route = Controller3.route) {
            LEDControllerScreen(controllerId = "3", ControllerType.FOURCHANNEL)
        }

        composable(route = Controller4.route) {
            LEDControllerScreen(controllerId = "4", ControllerType.RGBW)
        }

    }
}

fun NavHostController.navigateToController(route: String) =
    navigate(route) {
        launchSingleTop = true
        restoreState = true
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
    }