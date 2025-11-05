package com.example.peliculator

import com.example.MovieDetails
import com.example.MovieListResponse
import com.example.TvDetails
import com.example.TvListResponse
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.Locale

interface PelSerApiService {
    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(@Query("page") page: Int): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(@Query("page") page: Int): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(@Query("page") page: Int): MovieListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(@Path("movie_id") movieId: Int): MovieDetails

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(@Path("movie_id") movieId: Int): CreditsResponse

    @GET("movie/{movie_id}/similar")
    suspend fun getSimilarMovies(@Path("movie_id") movieId: Int): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): MovieListResponse

    @GET("tv/on_the_air")
    suspend fun getTvOnTheAir(@Query("page") page: Int): TvListResponse

    @GET("tv/popular")
    suspend fun getTvPopular(@Query("page") page: Int): TvListResponse

    @GET("tv/top_rated")
    suspend fun getTvTopRated(@Query("page") page: Int): TvListResponse

    @GET("tv/{tv_id}")
    suspend fun getTvDetails(@Path("tv_id") tvId: Int): TvDetails

    @GET("tv/{tv_id}/credits")
    suspend fun getTvCredits(@Path("tv_id") tvId: Int): CreditsResponse

    @GET("tv/{tv_id}/similar")
    suspend fun getSimilarTvShows(@Path("tv_id") tvId: Int): TvListResponse

    @GET("search/tv")
    suspend fun searchTvShows(@Query("query") query: String): TvListResponse

    @POST("account/{account_id}/favorite")
    suspend fun markAsFavorite(
        @Path("account_id") accountId: Int,
        @Query("media_type") mediaType: String,
        @Query("media_id") mediaId: Int,
        @Query("favorite") favorite: Boolean
    ): Response<Unit>

    @GET ("account/{account_id}/favorite/movies")
    suspend fun getFavoriteMovies(
        @Path("account_id") accountId: Int,
        @Query("page") page: Int = 1
    ): MovieListResponse

    @GET ("account/{account_id}/favorite/tv")
    suspend fun getFavoriteTvShows(
        @Path("account_id") accountId: Int,
        @Query("page") page: Int = 1
    ): TvListResponse

    @GET("person/{person_id}")
    suspend fun getActorDetails(@Path("person_id") personId: Int): ActorDetails

    @GET("person/{person_id}/combined_credits")
    suspend fun getCombinedCredits(@Path("person_id") personId: Int): CombinedCreditsResponse
}

private val client = OkHttpClient.Builder()
    .addInterceptor { chain ->
        val original = chain.request()
        val idioma = Locale.getDefault().language
        val url = original.url.newBuilder()
            .addQueryParameter("api_key", "1382adc7a9a0542913b40acd7b5282ed")
            .addQueryParameter("language", idioma)
            .build()
        val request = original.newBuilder().url(url).build()
        chain.proceed(request)
    }
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("https://api.themoviedb.org/3/")
    .client(client)
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api: PelSerApiService = retrofit.create(PelSerApiService::class.java)
