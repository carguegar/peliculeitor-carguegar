package com.example.peliculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.peliculator.ui.theme.PeliculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PeliculatorTheme {
                val navController = rememberNavController()
                AppScaffold(navController = navController) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "peliculas",
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = { fadeIn(animationSpec = tween(300)) },
                        exitTransition = { fadeOut(animationSpec = tween(300)) }
                    ) {
                        composable("peliculas") { PeliculasScreen(navController) }
                        composable("series") { SeriesScreen(navController) }
                        composable("favoritos") { FavoritosScreen(navController) }
                        composable(
                            route = "peliculaDetalle/{peliculaId}",
                            arguments = listOf(navArgument("peliculaId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val peliculaId = backStackEntry.arguments?.getInt("peliculaId")
                            requireNotNull(peliculaId) { "peliculaId parameter wasn't found." }
                            PeliculaDetallesScreen(peliculaId = peliculaId, navController = navController)
                        }
                        composable(
                            route = "serieDetalle/{serieId}",
                            arguments = listOf(navArgument("serieId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val serieId = backStackEntry.arguments?.getInt("serieId")
                            requireNotNull(serieId) { "serieId parameter wasn't found." }
                            SerieDetallesScreen(serieId = serieId, navController = navController)
                        }
                        composable(
                            route = "actorDetalle/{actorId}",
                            arguments = listOf(navArgument("actorId") { type = NavType.IntType })
                        ) { backStackEntry ->
                            val actorId = backStackEntry.arguments?.getInt("actorId")
                            requireNotNull(actorId) { "actorId parameter wasn't found." }
                            ActorDetalleScreen(actorId = actorId, navController = navController)
                        }
                    }
                }
            }
        }
    }
}
