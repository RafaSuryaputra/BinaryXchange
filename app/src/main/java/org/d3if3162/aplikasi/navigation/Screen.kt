package org.d3if3162.aplikasi.navigation

sealed class Screen(val route: String ) {
    data object Home: Screen("mainScreen")
    data object About: Screen("aboutScreen")
}