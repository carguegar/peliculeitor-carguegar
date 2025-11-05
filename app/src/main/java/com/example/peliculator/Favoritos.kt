package com.example.peliculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun FavoritosScreen(
    navController: NavController,
    viewModel: FavoritosViewModel = viewModel()
) {
    val favoriteMovies by viewModel.favoriteMovies.collectAsState()
    val favoriteTvShows by viewModel.favoriteTvShows.collectAsState()
    val watchlistItems by viewModel.watchlistItems.collectAsState()
    val imageBaseUrl = "https://image.tmdb.org/t/p/w500"

    if (favoriteMovies.isEmpty() && favoriteTvShows.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "No tienes favoritos todavía.")
        }
    } else {
        LazyColumn(modifier = Modifier.padding(8.dp)) {
            item {
                Text(
                    text = "Películas Favoritas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            item {
                if (favoriteMovies.isNotEmpty()) {
                    LazyRow {
                        items(favoriteMovies) { movie ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("peliculaDetalle/${movie.apiId}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${movie.posterPath}",
                                        contentDescription = "Poster de ${movie.title}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(vertical = 4.dp, horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = movie.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes películas favoritas.")
                    }
                }
            }

            item {
                Text(
                    text = "Series Favoritas",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            item {
                if (favoriteTvShows.isNotEmpty()) {
                    LazyRow {
                        items(favoriteTvShows) { tvShow ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("serieDetalle/${tvShow.apiId}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${tvShow.posterPath}",
                                        contentDescription = "Poster de ${tvShow.title}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(vertical = 4.dp, horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = tvShow.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes series favoritas.")
                    }
                }
            }
            item {
                Text(
                    text = "Watchlist",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                )
            }
            item{
                if (watchlistItems.isNotEmpty()) {
                    LazyRow {
                        items(watchlistItems) { watchlistItem ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate(if (watchlistItem.mediaType == "movie") "peliculaDetalle/${watchlistItem.apiId}" else "serieDetalle/${watchlistItem.apiId}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${watchlistItem.posterPath}",
                                        contentDescription = "Poster de ${watchlistItem.title}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.6f))
                                            .padding(vertical = 4.dp, horizontal = 2.dp)
                                    ) {
                                        Text(
                                            text = watchlistItem.title,
                                            color = Color.White,
                                            style = MaterialTheme.typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No tienes series en la lista de seguimiento.")
                    }
                }
            }
        }
    }
}
