package com.example.peliculator

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.MovieDetails
import com.example.ResultMovie
import com.example.peliculator.data.AppDatabase
import com.example.peliculator.data.Favorite
import com.example.peliculator.data.WatchlistItem
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class PeliculaDetalleViewModel(application: Application, private val peliculaId: Int) : ViewModel() {
    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    var pelicula by mutableStateOf<MovieDetails?>(null)
    var cast by mutableStateOf<List<Cast>>(emptyList())
    var similarMovies by mutableStateOf<List<ResultMovie>>(emptyList())

    val isFavorite = favoriteDao.getFavoriteById(peliculaId).map { it != null }
    val isWatchlist = favoriteDao.getWatchlistItemById(peliculaId).map { it != null }

    init {
        viewModelScope.launch {
            try {
                pelicula = api.getMovieDetails(peliculaId)
                cast = api.getMovieCredits(peliculaId).cast
                similarMovies = api.getSimilarMovies(peliculaId).results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            pelicula?.let {
                val favorite = favoriteDao.getFavoriteById(it.id).firstOrNull()
                if (favorite == null) {
                    favoriteDao.insert(Favorite(null,it.id, "movie", it.title, it.posterPath ?: ""))
                } else {
                    favoriteDao.delete(favorite)
                }
            }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            pelicula?.let {
                val watchlistItem = favoriteDao.getWatchlistItemById(it.id).firstOrNull()
                if (watchlistItem == null) {
                    favoriteDao.insertWatchlistItem(WatchlistItem(null,it.id, "movie", it.title, it.posterPath ?: ""))
                } else {
                    favoriteDao.deleteWatchlistItem(watchlistItem)
                }
            }
        }
    }

    class Factory(private val application: Application, private val peliculaId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(PeliculaDetalleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return PeliculaDetalleViewModel(application, peliculaId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun PeliculaDetallesScreen(peliculaId: Int, navController: NavController) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: PeliculaDetalleViewModel = viewModel(factory = PeliculaDetalleViewModel.Factory(application, peliculaId))

    val isFavorite by viewModel.isFavorite.collectAsState(initial = false)
    val isWatchlist by viewModel.isWatchlist.collectAsState(initial = false)
    val details = viewModel.pelicula

    Box(modifier = Modifier.fillMaxSize()) {
        if (details != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    val imageBaseUrl = "https://image.tmdb.org/t/p/w500"
                    Box {
                        AsyncImage(
                            model = "$imageBaseUrl${details.posterPath}",
                            contentDescription = stringResource(id = R.string.movie_poster_content_description, details.title),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2 / 3f)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(vertical = 4.dp, horizontal = 2.dp)
                        ) {
                            Text(
                                text = details.title,
                                color = Color.White,
                                style = typography.titleLarge,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(id = R.string.movie_details_synopsis),
                            style = typography.titleLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = details.overview,
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append(stringResource(id = R.string.movie_details_genres))
                                }
                                append(details.genres.joinToString { it.name })
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append(stringResource(id = R.string.movie_details_release_date))
                                }
                                append(details.releaseDate)
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append(stringResource(id = R.string.movie_details_rating))
                                }
                                append("${String.format("%.1f", details.voteAverage)}${stringResource(id = R.string.movie_details_rating_suffix)}")
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append(stringResource(id = R.string.movie_details_duration))
                                }
                                append("${details.runtime}${stringResource(id = R.string.movie_details_duration_suffix)}")
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.movie_details_cast), style = typography.titleLarge, modifier = Modifier.padding(16.dp))
                    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(viewModel.cast) { member ->
                            Column(
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .clickable { navController.navigate("actorDetalle/${member.id}") },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val imageUrl = "https://image.tmdb.org/t/p/w200${member.profilePath}"
                                AsyncImage(
                                    model = imageUrl,
                                    contentDescription = stringResource(id = R.string.cast_member_photo_content_description, member.name),
                                    modifier = Modifier
                                        .width(100.dp)
                                        .height(150.dp)
                                        .clip(CircleShape)
                                )
                                Text(text = member.name, style = typography.bodyMedium, textAlign = TextAlign.Center)
                                Text(text = member.character, style = typography.bodySmall, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.similar_movies), style = typography.titleLarge, modifier = Modifier.padding(16.dp))
                    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(viewModel.similarMovies) { movie ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("peliculaDetalle/${movie.id}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500${movie.posterPath}",
                                        contentDescription = stringResource(id = R.string.movie_poster_content_description, movie.title),
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
                                            style = typography.bodySmall,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier.fillMaxWidth(),
                                            maxLines = 2
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.loading_details))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(id = R.string.back_button_content_description),
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(8.dp)
                )
            }
            if (details != null) {
                Row() {
                    IconButton(onClick = { viewModel.toggleWatchlist() }) {
                        Icon(
                            imageVector = if (isWatchlist) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = stringResource(id = R.string.add_to_watchlist_content_description),
                            tint = Color.White,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                        )
                    }
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(id = R.string.mark_as_favorite_content_description),
                            tint = Color.White,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
