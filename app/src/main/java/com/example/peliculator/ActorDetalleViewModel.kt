package com.example.peliculator

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ActorDetalleViewModel(private val actorId: Int) : ViewModel() {
    var actorDetails by mutableStateOf<ActorDetails?>(null)
    var credits by mutableStateOf<List<MovieOrTvCredit>>(emptyList())

    init {
        viewModelScope.launch {
            try {
                actorDetails = api.getActorDetails(actorId)
                credits = api.getCombinedCredits(actorId).cast
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    class Factory(private val actorId: Int) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ActorDetalleViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ActorDetalleViewModel(actorId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
