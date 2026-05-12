package com.gabriel0011.asesmenmobpro.navigation

sealed class Screen(val route: String) {
    data object History : Screen("historyScreen")
    data object Home : Screen("mainScreen")
    data object About : Screen("aboutScreen")

    data object Update : Screen("update_screen/{id}") {
        fun withId(id: Long) = "update_screen/$id"
    }
}