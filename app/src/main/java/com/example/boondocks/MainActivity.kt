package com.example.boondocks

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.boondocks.data.Constants.ANTARCTICA
import com.example.boondocks.ui.components.TabRow
import com.example.boondocks.ui.theme.BoondocksTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoondocksApp()
        }

        collectLightsMessage()
    }

    private fun collectLightsMessage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.lightsMessageFlow.collect {
                    Log.i(ANTARCTICA, "received message $it")
                }
            }
        }
    }

    @Composable
    fun BoondocksApp() {
        BoondocksTheme {
            val navController = rememberNavController()
            val currentBackStack by navController.currentBackStackEntryAsState()
            val currentDestination = currentBackStack?.destination

            val currentScreen =
                tabRowScreens.find { it.route == currentDestination?.route } ?: Lights
            Scaffold(
                topBar = {
                    Column() {
                        Box(modifier = Modifier.statusBarsPadding())
                        TabRow(
                            allScreens = tabRowScreens,
                            onTabSelected = { newScreen ->
                                navController.navigateSingleTopTo(newScreen.route)
                            },
                            currentScreen = currentScreen,
                        )
                    }
                }

            ) { innerPadding ->
                BoondocksNavHost(
                    navController = navController,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }

    @Preview
    @Composable
    fun BoondocksAppPreview() {
        BoondocksApp()
    }
}

