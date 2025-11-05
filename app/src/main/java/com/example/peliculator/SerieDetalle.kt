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
import com.example.ResultTV
import com.example.TvDetails
import com.example.peliculator.data.AppDatabase
import com.example.peliculator.data.Favorite
import com.example.peliculator.data.WatchlistItem
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SerieDetalleViewModel(application: Application, private val serieId: Int) : ViewModel() {
    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()
    var serie by mutableStateOf<TvDetails?>(null)
    var cast by mutableStateOf<List<Cast>>(emptyList())
    var similarSeries by mutableStateOf<List<ResultTV>>(emptyList())

    val isFavorite = favoriteDao.getFavoriteById(serieId).map { it != null }
    val isWatchlist = favoriteDao.getWatchlistItemById(serieId).map { it != null }

    init {
        viewModelScope.launch {
            try {
                serie = api.getTvDetails(serieId)
                cast = api.getTvCredits(serieId).cast
                similarSeries = api.getSimilarTvShows(serieId).results
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            serie?.let {
                val favorite = favoriteDao.getFavoriteById(it.id).firstOrNull()
                if (favorite == null) {
                    favoriteDao.insert(Favorite(null, it.id, "tv", it.name, it.posterPath ?: ""))
                } else {
                    favoriteDao.delete(favorite)
                }
            }
        }
    }

    fun toggleWatchlist() {
        viewModelScope.launch {
            serie?.let {
                val watchlistItem = favoriteDao.getWatchlistItemById(it.id).firstOrNull()
                if (watchlistItem == null) {
                    favoriteDao.insertWatchlistItem(WatchlistItem(null,it.id, "tv", it.name, it.posterPath ?: ""))
                } else {
                    favoriteDao.deleteWatchlistItem(watchlistItem)
                }
            }
        }
    }

    class Factory(private val application: Application, private val serieId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SerieDetalleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SerieDetalleViewModel(application, serieId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

@Composable
fun SerieDetallesScreen(serieId: Int, navController: NavController) {
    val application = LocalContext.current.applicationContext as Application
    val viewModel: SerieDetalleViewModel = viewModel(factory = SerieDetalleViewModel.Factory(application, serieId))

    val isFavorite by viewModel.isFavorite.collectAsState(initial = false)
    val isWatchlist by viewModel.isWatchlist.collectAsState(initial = false)
    val details = viewModel.serie

    Box(modifier = Modifier.fillMaxSize()) {
        if (details != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    val imageBaseUrl = "https://image.tmdb.org/t/p/w500"
                    Box {
                        AsyncImage(
                            model = "$imageBaseUrl${details.posterPath}",
                            contentDescription = "Poster de ${details.name}",
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
                                text = details.name,
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
                            text = "Sinopsis",
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
                                    append("Géneros:\n")
                                }
                                append(details.genres.joinToString { it.name })
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append("Primer episodio:\n")
                                }
                                append(details.firstAirDate)
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append("Calificación:\n")
                                }
                                append("${String.format("%.1f", details.voteAverage)} / 10")
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append("Temporadas:\n")
                                }
                                append("${details.numberOfSeasons}")
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                         Text(
                            buildAnnotatedString {
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 16.sp)) {
                                    append("Episodios:\n")
                                }
                                append("${details.numberOfEpisodes}")
                            },
                            style = typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }
                }
                item {
                    Text(text = "Reparto", style = typography.titleLarge, modifier = Modifier.padding(16.dp))
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
                                    contentDescription = "Foto de ${member.name}",
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
                    Text(text = "Series similares", style = typography.titleLarge, modifier = Modifier.padding(16.dp))
                    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(viewModel.similarSeries) { serie ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("serieDetalle/${serie.id}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500${serie.posterPath}",
                                        contentDescription = "Poster de ${serie.name}",
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
                                            text = serie.name,
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
                Text("Cargando detalles...")
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
                    contentDescription = "Atrás",
                    tint = Color.White,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.5f))
                        .padding(8.dp)
                )
            }
            if (details != null) {
                Row(modifier = Modifier.padding(end = 16.dp)) {
                    IconButton(onClick = {viewModel.toggleWatchlist()}) {
                        Icon(
                            imageVector = if (isWatchlist) Icons.Default.Check else Icons.Default.Add,
                            contentDescription = "Agregar a la lista de seguimiento",
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
                            contentDescription = "Marcar como favorito",
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
