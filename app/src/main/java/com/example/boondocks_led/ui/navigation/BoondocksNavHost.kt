package com.example.boondocks_led.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.boondocks_led.data.DeviceConfiguration
import com.example.boondocks_led.data.getDefaultConfiguration
import com.example.boondocks_led.ui.ledcontroller.LEDControllerPage

@Composable
fun BoondocksNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    deviceConfig: DeviceConfiguration? = null
) {
    val config = deviceConfig ?: getDefaultConfiguration()

    NavHost(
        navController = navController,
        startDestination = Controller1.route,
        modifier = modifier
    ) {

        composable(route = Controller1.route) {
            LEDControllerPage(controllerId = "1", config = config.getController("1")!!)
        }

        composable(route = Controller2.route) {
            LEDControllerPage(controllerId = "2", config = config.getController("2")!!)
        }

        composable(route = Controller3.route) {
            LEDControllerPage(controllerId = "3", config = config.getController("3")!!)
        }

        composable(route = Controller4.route) {
            LEDControllerPage(controllerId = "4", config = config.getController("4")!!)
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