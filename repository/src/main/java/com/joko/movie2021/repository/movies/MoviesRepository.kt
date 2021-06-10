package com.joko.movie2021.repository.movies

import com.joko.movie2021.core.NetworkBoundResource
import com.joko.movie2021.core.Resource
import com.joko.movie2021.repository.actors.Actor
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.zipWith

class MoviesRepository internal constructor(
    private val localMoviesSource: LocalMoviesSource,
    private val remoteMoviesSource: RemoteMoviesSource
) {

    fun getMovieDetailsFlowable(id: Int): NetworkBoundResource<Movie> {

        return object : NetworkBoundResource<Movie>() {
            override fun fetchFromNetwork(): Flowable<Resource<Movie>> {
                return remoteMoviesSource.getMovieDetails(id).toFlowable()
            }

            override fun fetchFromDatabase(): Flowable<Resource<Movie>> {
                return localMoviesSource.getMovieFlowable(id)
                    .map { movie -> Resource.Success(movie) }
            }

            override fun shouldRefresh(): Single<Boolean> {
                val isInDb = localMoviesSource.isMovieInDatabase(id).map { count -> count == 0 }
                val isModelComplete = localMoviesSource
                    .getMovie(id)
                    .map { movie -> movie.isModelComplete }
                    .onErrorReturn { false }

                return isInDb
                    .zipWith(isModelComplete) { dbStatus, modelStatus ->
                        !(dbStatus && modelStatus)
                    }
            }

            override fun saveToDatabase(movie: Movie) {
                localMoviesSource.saveMovieToDatabase(movie)
            }
        }
    }

    fun getMovieCast(movieId: Int): NetworkBoundResource<Cast> {

        return object : NetworkBoundResource<Cast>() {
            override fun fetchFromNetwork(): Flowable<Resource<Cast>> {
                return remoteMoviesSource.getMovieCast(movieId).toFlowable()
            }

            override fun fetchFromDatabase(): Flowable<Resource<Cast>> {
                return localMoviesSource
                    .getCastForMovieFlowable(movieId)
                    .map { cast ->
                        Resource.Success(cast)
                    }
            }

            override fun shouldRefresh(): Single<Boolean> {
                return localMoviesSource
                    .isCastInDatabase(movieId)
                    .map { count -> count == 0 }
            }

            override fun saveToDatabase(cast: Cast) {
                localMoviesSource.saveCastToDatabase(cast)
            }
        }
    }

    fun forceRefreshMovieDetails(id: Int): NetworkBoundResource<Movie> {
        return object : NetworkBoundResource<Movie>() {
            override fun fetchFromNetwork(): Flowable<Resource<Movie>> {
                return remoteMoviesSource.getMovieDetails(id).toFlowable()
            }

            override fun fetchFromDatabase(): Flowable<Resource<Movie>> {
                return localMoviesSource.getMovieFlowable(id)
                    .map { movie -> Resource.Success(movie) }
            }

            override fun shouldRefresh(): Single<Boolean> {
                return Single.just(true)
            }

            override fun saveToDatabase(movie: Movie) {
                localMoviesSource.saveMovieToDatabase(movie)
            }
        }
    }

    fun getActorsInMovie(ids: List<Int>): Single<List<Resource<Actor>>> {
        return localMoviesSource.getActorsForMovie(ids)
            .map { actors -> actors.map { Resource.Success(it) } }
    }
}