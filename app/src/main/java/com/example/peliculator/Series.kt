package com.example.peliculator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage

@Composable
fun SeriesScreen(
    navController: NavController,
    viewModel: SeriesViewModel = viewModel()
) {
    val imageBaseUrl = "https://image.tmdb.org/t/p/w500"
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val lazyListState = rememberLazyListState()
    val lazyRowState1 = rememberLazyListState()
    val lazyRowState2 = rememberLazyListState()
    val lazyRowState3 = rememberLazyListState()

    LaunchedEffect(lazyListState.isScrollInProgress || lazyRowState1.isScrollInProgress || lazyRowState2.isScrollInProgress || lazyRowState3.isScrollInProgress){
        if (lazyListState.isScrollInProgress || lazyRowState1.isScrollInProgress || lazyRowState2.isScrollInProgress || lazyRowState3.isScrollInProgress) {
            focusManager.clearFocus()
            viewModel.dismissSearchDropdown()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.clearSearch()
        }
    }

    Column {
        Box {
            TextField(
                value = viewModel.searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                label = { Text("Buscar series") },
                leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search Icon") },
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .focusRequester(focusRequester)
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            viewModel.onSearchFocus()
                        }
                    }
            )
            DropdownMenu(
                expanded = viewModel.isSearchDropdownVisible && viewModel.searchResults.isNotEmpty(),
                onDismissRequest = {
                    viewModel.dismissSearchDropdown()
                 },
                properties = PopupProperties(focusable = false),
                modifier = Modifier.fillMaxWidth()
            ) {
                viewModel.searchResults.forEach { serie ->
                    DropdownMenuItem(text = { Text(serie.name) }, onClick = {
                        navController.navigate("serieDetalle/${serie.id}")
                        viewModel.clearSearch()
                        focusManager.clearFocus()
                    })
                }
            }
        }

        LazyColumn(modifier = Modifier.padding(8.dp), state = lazyListState) {
            item {
                Text(text = "Series En Emisión", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
                if (viewModel.seriesActuales.isEmpty()) {
                    LazyRow {
                        items(5) { PlaceholderItem() }
                    }
                } else {
                    LazyRow(state = lazyRowState1) {
                        itemsIndexed(viewModel.seriesActuales) { index, serie ->
                            if (index == viewModel.seriesActuales.size - 1) {
                                LaunchedEffect(Unit) { viewModel.loadMoreSeriesActuales() }
                            }
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("serieDetalle/${serie.id}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${serie.posterPath}",
                                        contentDescription = "Poster de la serie ${serie.name}",
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
            item {
                Text(text = "Series Populares", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
                if (viewModel.seriesPopulares.isEmpty()) {
                    LazyRow {
                        items(5) { PlaceholderItem() }
                    }
                } else {
                    LazyRow(state = lazyRowState2) {
                        itemsIndexed(viewModel.seriesPopulares) { index, serie ->
                            if (index == viewModel.seriesPopulares.size - 1) {
                                LaunchedEffect(Unit) { viewModel.loadMoreSeriesPopulares() }
                            }
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("serieDetalle/${serie.id}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${serie.posterPath}",
                                        contentDescription = "Poster de la serie ${serie.name}",
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
            item {
                Text(text = "Series Mejor Valoradas", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(8.dp))
                if (viewModel.seriesTopRated.isEmpty()) {
                    LazyRow {
                        items(5) { PlaceholderItem() }
                    }
                } else {
                    LazyRow(state = lazyRowState3) {
                        itemsIndexed(viewModel.seriesTopRated) { index, serie ->
                            if (index == viewModel.seriesTopRated.size - 1) {
                                LaunchedEffect(Unit) { viewModel.loadMoreSeriesTopRated() }
                            }
                            Card(
                                modifier = Modifier.padding(4.dp),
                                onClick = { navController.navigate("serieDetalle/${serie.id}") }
                            ) {
                                Box(modifier = Modifier.width(120.dp).height(180.dp)) {
                                    AsyncImage(
                                        model = "$imageBaseUrl${serie.posterPath}",
                                        contentDescription = "Poster de la serie ${serie.name}",
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
        }
    }
}
