package com.joko.movie2021.repository.collections

import com.joko.movie2021.core.NetworkBoundResource
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.repository.movies.LocalMoviesSource
import com.joko.movie2021.repository.movies.Movie
import io.reactivex.Flowable
import io.reactivex.Single

class CollectionsRepository internal constructor(
    private val localCollectionsSource: LocalCollectionsSource,
    private val remoteCollectionsSource: RemoteCollectionsSource,
    private val localMoviesResource: LocalMoviesSource
) {

    fun searchPopularCollectionFlowable(query: String): Flowable<Resource.Success<List<Movie>>> {
        return localCollectionsSource.getCollectionFlowable(CollectionType.Popular)
            .switchMap {
                localCollectionsSource.getMoviesForCollectionFlowable(it.contents)
            }
            .map {
                val filter = it.filter { movie -> movie.title.contains(query, true) }
                Resource.Success(filter)
            }
    }

    /**
     * This method should be used with care because the upstream observables it is working on may not emit onComplete,
     * and the downstream might suffer because of it
     */
    fun getCollectionFlowable(
        accountId: Int = 0,
        type: CollectionType,
        region: String = ""
    ): NetworkBoundResource<List<Movie>> {

        return object : NetworkBoundResource<List<Movie>>() {
            override fun fetchFromNetwork(): Flowable<Resource<List<Movie>>> {
                return remoteCollectionsSource.getCollection(accountId, type, region)
                    .flatMap { response ->
                        Single.just(
                            when (response) {
                                is Resource.Success -> Resource.Success(
                                    response.data.movies ?: emptyList()
                                )
                                is Resource.Error -> Resource.Error(response.errorMessage)
                                is Resource.Loading -> Resource.Loading()
                            }
                        )
                    }
                    .toFlowable()
            }

            override fun fetchFromDatabase(): Flowable<Resource<List<Movie>>> {
                return localCollectionsSource.getCollectionFlowable(type)
                    .switchMap { collection ->
                        localCollectionsSource.getMoviesForCollectionFlowable(collection.contents)
                    }
                    .map { moviesInCollection ->
                        Resource.Success(moviesInCollection)
                    }
            }

            override fun shouldRefresh(): Single<Boolean> {
                return localCollectionsSource.isCollectionInDatabase(type)
                    .map { count -> count == 0 }
            }

            override fun saveToDatabase(movies: List<Movie>) {
                return localMoviesResource.saveMoviesToDatabase(movies)
            }
        }
    }

    fun forceRefreshCollection(
        accountId: Int = 0,
        type: CollectionType,
        region: String = ""
    ): NetworkBoundResource<Collection> {
        log("Force refreshing collection")
        return object : NetworkBoundResource<Collection>() {
            override fun fetchFromNetwork(): Flowable<Resource<Collection>> {
                return remoteCollectionsSource.getCollection(accountId, type, region).toFlowable()
            }

            override fun fetchFromDatabase(): Flowable<Resource<Collection>> {
                return localCollectionsSource
                    .getCollectionFlowable(type)
                    .map { collection -> Resource.Success(collection) }
            }

            override fun shouldRefresh(): Single<Boolean> {
                return Single.just(true)
            }

            override fun saveToDatabase(collection: Collection) {
                localCollectionsSource.saveCollection(collection)
            }
        }
    }
}