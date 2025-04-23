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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.boondocks.data.Constants.ANTARCTICA
import com.example.boondocks.ui.components.TabRow
import com.example.boondocks.ui.lights.LightsViewModel
import com.example.boondocks.ui.theme.BoondocksTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    //this will be mainactivity view model
    private val lightsViewModel: LightsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BoondocksApp()
        }

        collectLightsMessage()
    }

    //put this in viewmodel and scope it to that (or maybe not, just observe in viewmodel and take action here)
    private fun collectLightsMessage() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                lightsViewModel.lightsMessageFlow.collect { message ->
                    Log.i(ANTARCTICA, "In the activity, received this:   $message")
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

//repository with the flow, then an activity viewmodels coped to the activity, then inject same repository into both viewmodels
// mainactivity AndroidViewModel AND screen viewmodel need to inject the repository and do emission from there.
//need to set up hilt DI for this
// screen viewmodel tells repository what message to emit
//in repository addMessage(message)

/*
interface LightRepository {
val messageFlow: SharedFlow<String>

}
fun lightAdded(message: String)

in mainactivity lightsrepository.messageflow.collect


read about bridge pattern interface
read about hilt setup DI
 */
