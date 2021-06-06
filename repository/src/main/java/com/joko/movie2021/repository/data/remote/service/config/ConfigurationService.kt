package com.joko.movie2021.repository.data.remote.service.config

import io.reactivex.Single
import retrofit2.http.GET

internal interface ConfigurationService {

    @GET("configuration")
    fun getConfiguration(): Single<ConfigurationResponse>

}