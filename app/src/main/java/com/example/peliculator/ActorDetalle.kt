package com.example.peliculator

import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun ActorDetalleScreen(actorId: Int, navController: NavController) {
    val viewModel: ActorDetalleViewModel = viewModel(factory = ActorDetalleViewModel.Factory(actorId))
    val actorDetails = viewModel.actorDetails
    val credits = viewModel.credits

    Box(modifier = Modifier.fillMaxSize()) {
        if (actorDetails != null) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        val imageUrl = "https://image.tmdb.org/t/p/w500${actorDetails.profilePath}"
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = stringResource(id = R.string.cast_member_photo_content_description, actorDetails.name),
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2 / 3f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black.copy(alpha = 0.5f))
                                .padding(16.dp)
                                .align(Alignment.BottomCenter)
                        ) {
                            Text(
                                text = actorDetails.name,
                                style = typography.titleLarge,
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = stringResource(id = R.string.actor_details_biography), style = typography.titleMedium)
                        Text(text = actorDetails.biography, style = typography.bodyMedium)
                    }
                }
                item {
                    Text(text = stringResource(id = R.string.actor_details_known_for), style = typography.titleMedium, modifier = Modifier.padding(16.dp))
                    LazyRow(modifier = Modifier.padding(horizontal = 16.dp)) {
                        items(credits) { credit ->
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = {
                                    if (credit.mediaType == "movie") {
                                        navController.navigate("peliculaDetalle/${credit.id}")
                                    } else {
                                        navController.navigate("serieDetalle/${credit.id}")
                                    }
                                }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "https://image.tmdb.org/t/p/w500${credit.posterPath}",
                                        contentDescription = stringResource(id = R.string.credit_poster_content_description, credit.title ?: credit.name ?: ""),
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
                                            text = credit.title ?: credit.name ?: "",
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
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(id = R.string.loading_actor_details))
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
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
        }
    }
}
