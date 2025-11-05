package com.example.peliculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ResultTV
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SeriesViewModel : ViewModel() {
    var seriesActuales by mutableStateOf<List<ResultTV>>(emptyList())
    private var pageActuales = 1
    private var isLoadingActuales = false

    var seriesPopulares by mutableStateOf<List<ResultTV>>(emptyList())
    private var pagePopulares = 1
    private var isLoadingPopulares = false

    var seriesTopRated by mutableStateOf<List<ResultTV>>(emptyList())
    private var pageTopRated = 1
    private var isLoadingTopRated = false

    var searchQuery by mutableStateOf("")
    var searchResults by mutableStateOf<List<ResultTV>>(emptyList())
    var isSearchDropdownVisible by mutableStateOf(false)
    private var searchJob: Job? = null

    init {
        loadMoreSeriesActuales()
        loadMoreSeriesPopulares()
        loadMoreSeriesTopRated()
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        isSearchDropdownVisible = true
        searchJob?.cancel()
        if (query.length >= 3) {
            searchJob = viewModelScope.launch {
                delay(300)
                try {
                    searchResults = api.searchTvShows(query).results.take(5)
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

    fun loadMoreSeriesActuales() {
        if (isLoadingActuales) return
        isLoadingActuales = true
        viewModelScope.launch {
            try {
                val newSeries = api.getTvOnTheAir(pageActuales).results
                if (newSeries.isNotEmpty()) {
                    seriesActuales = seriesActuales + newSeries
                    pageActuales++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingActuales = false
            }
        }
    }

    fun loadMoreSeriesPopulares() {
        if (isLoadingPopulares) return
        isLoadingPopulares = true
        viewModelScope.launch {
            try {
                val newSeries = api.getTvPopular(pagePopulares).results
                if (newSeries.isNotEmpty()) {
                    seriesPopulares = seriesPopulares + newSeries
                    pagePopulares++
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoadingPopulares = false
            }
        }
    }

    fun loadMoreSeriesTopRated() {
        if (isLoadingTopRated) return
        isLoadingTopRated = true
        viewModelScope.launch {
            try {
                val newSeries = api.getTvTopRated(pageTopRated).results
                if (newSeries.isNotEmpty()) {
                    seriesTopRated = seriesTopRated + newSeries
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
