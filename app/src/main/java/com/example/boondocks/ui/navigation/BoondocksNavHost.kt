package com.example.boondocks.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.boondocks.ui.fridge.FridgeScreen
import com.example.boondocks.ui.lights.LightsScreen
import com.example.boondocks.ui.motors.MotorsScreen
import com.example.boondocks.ui.water.WaterScreen

@Composable
fun BoondocksNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Lights.route,
        modifier = modifier
    ) {
        composable(route = Lights.route) {
            LightsScreen()
        }
        composable(route = Motors.route) {
            MotorsScreen()
        }
        composable(route = Water.route) {
            WaterScreen()
        }
        composable(route = Fridge.route) {
            FridgeScreen()
        }
    }
}

fun NavHostController.navigateSingleTopTo(route: String) =
    this.navigate(route) {
        launchSingleTop = true
    }