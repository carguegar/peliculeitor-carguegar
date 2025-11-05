package com.example.peliculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ResultMovie
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PeliculasViewModel : ViewModel() {
    var peliculasCartelera by mutableStateOf<List<ResultMovie>>(emptyList())
    private var pageCartelera = 1
    private var isLoadingCartelera = false

    var peliculasProximas by mutableStateOf<List<ResultMovie>>(emptyList())
    private var pageProximas = 1
    private var isLoadingProximas = false

    var peliculasTopRated by mutableStateOf<List<ResultMovie>>(emptyList())
    private var pageTopRated = 1
    private var isLoadingTopRated = false

    var searchQuery by mutableStateOf("")
    var searchResults by mutableStateOf<List<ResultMovie>>(emptyList())
    var isSearchDropdownVisible by mutableStateOf(false)
    private var searchJob: Job? = null

    init {
        loadMorePeliculasCartelera()
        loadMorePeliculasProximas()
        loadMorePeliculasTopRated()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        isSearchDropdownVisible = true
        searchJob?.cancel()
        if (query.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(300)
                try {
                    searchResults = api.searchMovies(query).results.take(5)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            searchResults = emptyList()
        }
    }

    fun onSearchFocus() {
        if (searchQuery.length >= 3) {
            isSearchDropdownVisible = true
            onSearchQueryChange(searchQuery)
        }
    }

    fun dismissSearchDropdown() {
        isSearchDropdownVisible = false
    }

    fun clearSearch() {
        searchQuery = ""
        searchResults = emptyList()
        isSearchDropdownVisible = false
    }

    fun loadMorePeliculasCartelera() {
        if (isLoadingCartelera) return
        isLoadingCartelera = true
        viewModelScope.launch {
            try {
                val newPeliculas = api.getNowPlayingMovies(pageCartelera).results
                if (newPeliculas.isNotEmpty()) {
                    peliculasCartelera = peliculasCartelera + newPeliculas
                    pageCartelera++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingCartelera = false
            }
        }
    }

    fun loadMorePeliculasProximas() {
        if (isLoadingProximas) return
        isLoadingProximas = true
        viewModelScope.launch {
            try {
                val newPeliculas = api.getUpcomingMovies(pageProximas).results
                 if (newPeliculas.isNotEmpty()) {
                    peliculasProximas = peliculasProximas + newPeliculas
                    pageProximas++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingProximas = false
            }
        }
    }

    fun loadMorePeliculasTopRated() {
        if (isLoadingTopRated) return
        isLoadingTopRated = true
        viewModelScope.launch {
            try {
                val newPeliculas = api.getTopRatedMovies(pageTopRated).results
                if (newPeliculas.isNotEmpty()) {
                    peliculasTopRated = peliculasTopRated + newPeliculas
                    pageTopRated++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingTopRated = false
            }
        }
    }
}
