package com.joko.movie2021.repository.data.remote.service.search

import com.haroldadmin.cnradapter.NetworkResponse
import com.joko.movie2021.repository.data.remote.service.common.ErrorResponse
import kotlinx.coroutines.Deferred
import retrofit2.http.GET
import retrofit2.http.Query

internal interface SearchService {

    @GET("search/movie")
    fun searchForMovie(
        @Query("query") query: String,
        @Query("page") page: Int = 1
    ): Deferred<NetworkResponse<SearchResponse, ErrorResponse>>

}