package com.example.peliculator

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class Screen(val route: String) {
    object Peliculas : Screen("peliculas")
    object Series : Screen("series")
    object Favoritos : Screen("favoritos")
}

private val items = listOf(
    Screen.Peliculas,
    Screen.Series,
    Screen.Favoritos,
)

@Composable
fun AppScaffold(
    navController: NavController,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = { AppBottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun AppBottomNavigationBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar {
        items.forEach { screen ->
            NavigationBarItem(
                icon = {
                    when (screen) {
                        Screen.Peliculas -> Icon(Icons.Filled.Movie, contentDescription = "Películas")
                        Screen.Series -> Icon(Icons.Filled.LiveTv, contentDescription = "Series")
                        Screen.Favoritos -> Icon(Icons.Filled.Favorite, contentDescription = "Favoritos")
                    }
                },
                label = {
                    when (screen) {
                        Screen.Peliculas -> Text("Películas")
                        Screen.Series -> Text("Series")
                        Screen.Favoritos -> Text("Favoritos")
                    }
                },
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = false 
                    }
                }
            )
        }
    }
}
