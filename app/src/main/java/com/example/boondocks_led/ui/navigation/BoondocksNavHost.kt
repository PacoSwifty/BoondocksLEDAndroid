package com.example.boondocks_led.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.boondocks_led.data.ControllerType
import com.example.boondocks_led.ui.ledcontroller.LEDControllerPage

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
            LEDControllerPage(controllerId = "1", type = ControllerType.RGBW)
        }

        composable(route = Controller2.route) {
            LEDControllerPage(controllerId = "2", type = ControllerType.RGBPLUS1)
        }

        composable(route = Controller3.route) {
            LEDControllerPage(controllerId = "3", type = ControllerType.FOURCHANNEL)
        }

        composable(route = Controller4.route) {
            LEDControllerPage(controllerId = "4", type = ControllerType.RGBW)
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