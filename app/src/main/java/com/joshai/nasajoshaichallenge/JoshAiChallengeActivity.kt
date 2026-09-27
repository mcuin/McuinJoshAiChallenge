package com.joshai.nasajoshaichallenge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.joshai.nasajoshaichallenge.dataClasses.RoverDetailRoute
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
                    composable<RoverDetailRoute> {
                        RoverDetailScreen(navController = navController)
                    }
                }
            }
        }
    }
}