package com.gabriel0011.asesmenmobpro.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.gabriel0011.asesmenmobpro.ui.screen.MainScreen
import com.gabriel0011.asesmenmobpro.ui.screen.HistoryScreen
import com.gabriel0011.asesmenmobpro.ui.screen.UpdateScreen

@Composable
fun SetupNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(
        navController = navController,
        startDestination = Screen.History.route
    ) {
        composable(route = Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable(route = Screen.Home.route) {
            MainScreen(navController)
        }
        composable(route = Screen.About.route) {
            AboutScreen(navController)
        }
        composable(
            route = Screen.Update.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: 0L
            UpdateScreen(navController = navController, id = id)
        }

    }
}