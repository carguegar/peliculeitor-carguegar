package com.example.peliculator.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {
    @Query("SELECT * FROM favorites WHERE mediaType = 'movie'")
    fun getFavoriteMovies(): Flow<List<Favorite>>

    @Query("SELECT * FROM favorites WHERE mediaType = 'tv'")
    fun getFavoriteTvShows(): Flow<List<Favorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(favorite: Favorite)

    @Delete
    suspend fun delete(favorite: Favorite)

    @Query("SELECT * FROM favorites WHERE apiId = :id")
    fun getFavoriteById(id: Int): Flow<Favorite?>

    @Query("SELECT * FROM watchlist")
    fun getWatchlistItems(): Flow<List<WatchlistItem>>

    @Query("SELECT * FROM watchlist WHERE apiId = :id")
    fun getWatchlistItemById(id: Int): Flow<WatchlistItem?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWatchlistItem(item: WatchlistItem)

    @Delete
    suspend fun deleteWatchlistItem(item: WatchlistItem)
}
