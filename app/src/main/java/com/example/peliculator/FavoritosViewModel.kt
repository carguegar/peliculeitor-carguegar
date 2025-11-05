package com.example.peliculator

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.peliculator.data.AppDatabase
import com.example.peliculator.data.Favorite
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class FavoritosViewModel(application: Application) : AndroidViewModel(application) {
    private val favoriteDao = AppDatabase.getDatabase(application).favoriteDao()

    val favoriteMovies = favoriteDao.getFavoriteMovies()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTvShows = favoriteDao.getFavoriteTvShows()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val watchlistItems = favoriteDao.getWatchlistItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
