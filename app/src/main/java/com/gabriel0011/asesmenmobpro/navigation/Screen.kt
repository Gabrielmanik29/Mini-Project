package com.gabriel0011.asesmenmobpro.navigation

sealed class Screen(val route: String) {
    data object History : Screen("historyScreen")
    data object Home : Screen("mainScreen")
    data object About : Screen("aboutScreen")
}