package com.joko.movie2021.repository.data.remote.service.discover

import com.haroldadmin.cnradapter.NetworkResponse
import com.joko.movie2021.repository.data.remote.service.common.ErrorResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

private val df = SimpleDateFormat("yyyy-MM-dd", Locale.US)
private val currentDate: String = df.format(Date())
private val pastDate = run {
    val day = Calendar.getInstance().apply { add(Calendar.MONTH, -1) }
    df.format(Date(day.timeInMillis))
}

internal interface DiscoveryService {

    @GET("discover/movie")
    fun getMoviesInTheatre(
        @Query("sort_by") sortBy: String = SortParameters.PopularityDsc,
        @Query("page") page: Int = 1,
        @Query("primary_release_date.lte") releaseDateGte: String = currentDate,
        @Query("primary_release_date.gte") releaseDateLte: String = pastDate
    ): Deferred<NetworkResponse<DiscoverMoviesResponse, ErrorResponse>>

    @GET("movie/popular")
    fun getPopularMovies(): Deferred<NetworkResponse<DiscoverMoviesResponse, ErrorResponse>>

    @GET("movie/top_rated")
    fun getTopRatedMovies(): Deferred<NetworkResponse<DiscoverMoviesResponse, ErrorResponse>>

    @GET("movie/upcoming")
    fun getUpcomingMovies(): Deferred<NetworkResponse<DiscoverMoviesResponse, ErrorResponse>>

    @GET("movie/now_playing")
    fun getNowPlayingMovies(): Deferred<NetworkResponse<DiscoverMoviesResponse, ErrorResponse>>

}