package com.joshai.nasajoshaichallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.joshai.nasajoshaichallenge.dataClasses.FullRoverData
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute
import com.joshai.nasajoshaichallenge.dataClasses.RoverId
import com.joshai.nasajoshaichallenge.dataClasses.RoversListRoute
import com.joshai.nasajoshaichallenge.ui.theme.NASAJoshAIChallengeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class JoshAiChallengeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NASAJoshAIChallengeTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = RoversListRoute) {
                    composable<RoversListRoute> {
                        NASARoverListScreen(navController)
                    }
                    composable<RoverDetailRoute> { backStackEntry ->
                        val route: RoverDetailRoute = backStackEntry.toRoute()
                        val roverId = route.roverId
                        RoverDetailScreen(navController = navController, roverId = roverId)
                    }
                }
            }
        }
    }
}