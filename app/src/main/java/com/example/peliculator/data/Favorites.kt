package com.example.peliculator.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class Favorite(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val apiId: Int,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val posterPath: String
)

@Entity(tableName = "watchlist")
data class WatchlistItem(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    val apiId: Int,
    val mediaType: String, // "movie" or "tv"
    val title: String,
    val posterPath: String
)