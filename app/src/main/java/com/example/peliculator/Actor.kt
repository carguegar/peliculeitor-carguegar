package com.example.peliculator

import com.google.gson.annotations.SerializedName

data class ActorDetails(
    val id: Int,
    val name: String,
    val biography: String,
    @SerializedName("profile_path") val profilePath: String?
)

data class CombinedCreditsResponse(
    val cast: List<MovieOrTvCredit>
)

data class MovieOrTvCredit(
    val id: Int,
    val title: String?, // For movies
    val name: String?, // For TV shows
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("media_type") val mediaType: String // "movie" or "tv"
)
