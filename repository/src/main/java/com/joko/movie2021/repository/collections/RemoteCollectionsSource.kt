package com.joko.movie2021.repository.collections

import com.haroldadmin.cnradapter.NetworkResponse
import com.joko.movie2021.core.Resource
import com.joko.movie2021.repository.data.remote.service.discover.DiscoveryService
import com.joko.movie2021.repository.toMovie
import io.reactivex.Single
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.rx2.asSingle

internal class RemoteCollectionsSource(
    private val discoveryService: DiscoveryService,
) {

    fun getCollection(type: CollectionType): Single<Resource<Collection>> {
        return when (type) {
            CollectionType.Popular -> getPopularCollection()
            CollectionType.Upcoming -> getUpcomingCollection()
            CollectionType.InTheatres -> getInTheatresCollection()
            CollectionType.TopRated -> getTopRatedCollection()
            CollectionType.NowPlaying -> getNowPlayingCollection()
            else -> Single.just(Resource.Error("Wring collection type"))
        }
    }

    private fun getPopularCollection(): Single<Resource<Collection>> {
        return discoveryService.getPopularMovies().asSingle(Dispatchers.Default)
            .flatMap { response ->
                Single.just(when (response) {
                    is NetworkResponse.Success -> {
                        Resource.Success(
                            Collection(
                                CollectionType.Popular.name,
                                response.body.results.map { it.id }
                            ).apply {
                                this.movies = response.body.results.map { it.toMovie() }
                            }
                        )
                    }
                    is NetworkResponse.ServerError -> {
                        Resource.Error(response.body?.statusMessage ?: "Server Error")
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.Error(response.error.localizedMessage ?: "Network Error")
                    }
                }
                )
            }
    }

    private fun getUpcomingCollection(): Single<Resource<Collection>> {
        return discoveryService.getUpcomingMovies().asSingle(Dispatchers.Default)
            .flatMap { response ->
                Single.just(when (response) {
                    is NetworkResponse.Success -> {
                        Resource.Success(
                            Collection(
                                CollectionType.Upcoming.name,
                                response.body.results.map { it.id }
                            ).apply {
                                this.movies = response.body.results.map { it.toMovie() }
                            }
                        )
                    }
                    is NetworkResponse.ServerError -> {
                        Resource.Error(response.body?.statusMessage ?: "Server Error")
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.Error(response.error.localizedMessage ?: "Network Error")
                    }
                }
                )
            }
    }

    private fun getTopRatedCollection(): Single<Resource<Collection>> {
        return discoveryService.getTopRatedMovies().asSingle(Dispatchers.Default)
            .flatMap { response ->
                Single.just(when (response) {
                    is NetworkResponse.Success -> {
                        Resource.Success(
                            Collection(
                                CollectionType.TopRated.name,
                                response.body.results.map { it.id }
                            ).apply {
                                this.movies = response.body.results.map { it.toMovie() }
                            }
                        )
                    }
                    is NetworkResponse.ServerError -> {
                        Resource.Error(response.body?.statusMessage ?: "Server Error")
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.Error(response.error.localizedMessage ?: "Network Error")
                    }
                }
                )
            }
    }

    private fun getNowPlayingCollection(): Single<Resource<Collection>> {
        return discoveryService.getNowPlayingMovies().asSingle(Dispatchers.Default)
            .flatMap { response ->
                Single.just(when (response) {
                    is NetworkResponse.Success -> {
                        Resource.Success(
                            Collection(
                                CollectionType.NowPlaying.name,
                                response.body.results.map { it.id }
                            ).apply {
                                this.movies = response.body.results.map { it.toMovie() }
                            }
                        )
                    }
                    is NetworkResponse.ServerError -> {
                        Resource.Error(response.body?.statusMessage ?: "Server Error")
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.Error(response.error.localizedMessage ?: "Network Error")
                    }
                }
                )
            }
    }

    private fun getInTheatresCollection(): Single<Resource<Collection>> {
        return discoveryService.getMoviesInTheatre()
            .asSingle(Dispatchers.Default)
            .flatMap { topRatedResponse ->
                Single.just(
                    when (topRatedResponse) {
                        is NetworkResponse.Success -> {
                            Resource.Success(
                                Collection(
                                    CollectionType.InTheatres.name,
                                    topRatedResponse.body.results.map { it.id })
                                    .apply {
                                        movies = topRatedResponse.body.results.map { it.toMovie() }
                                }
                        )
                    }
                    is NetworkResponse.ServerError -> {
                        Resource.Error<Collection>(
                            topRatedResponse.body?.statusMessage ?: "Server Error"
                        )
                    }
                    is NetworkResponse.NetworkError -> {
                        Resource.Error(
                            topRatedResponse.error.localizedMessage ?: "Network Error"
                        )
                    }
                }
                )
            }
    }
}