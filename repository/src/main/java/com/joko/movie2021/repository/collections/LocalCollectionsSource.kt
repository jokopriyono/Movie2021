package com.joko.movie2021.repository.collections

import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.repository.movies.MovieDao
import io.reactivex.Flowable
import io.reactivex.Single

internal class LocalCollectionsSource(
    private val collectionsDao: CollectionDao,
    private val moviesDao: MovieDao
) {

    fun getCollectionFlowable(type: CollectionType): Flowable<Collection> {
        return when (type) {
            CollectionType.Favourite -> collectionsDao.getFavouriteMoviesFlowable()
            CollectionType.Watchlist -> collectionsDao.getWatchlistedMoviesFlowable()
            CollectionType.Popular -> collectionsDao.getPopularMoviesFlowable()
            CollectionType.Upcoming -> collectionsDao.getUpcomingMoviesFlowable()
            CollectionType.InTheatres -> collectionsDao.getMoviesInTheatresFlowable()
        }
    }

    fun getCollection(type: CollectionType): Single<Collection> {
        return when (type) {
            CollectionType.Favourite -> collectionsDao.getFavouriteMovies()
            CollectionType.Watchlist -> collectionsDao.getWatchlistedMovies()
            CollectionType.Popular -> collectionsDao.getPopularMovies()
            CollectionType.Upcoming -> collectionsDao.getUpcomingMovies()
            CollectionType.InTheatres -> collectionsDao.getMoviesInTheatres()
        }
    }

    fun getMoviesForCollectionFlowable(ids: List<Int>): Flowable<List<Movie>> {
        log("Querying DB for collection movies: $ids")
        return collectionsDao.getMoviesForCollectionFlowable(ids)
    }

    fun saveMoviesInCollection(collection: Collection) {
        log("Saving movies in collection")
        collection.movies?.let {
            moviesDao.saveAllMoviesFromCollection(it)
        }
    }

    fun saveCollection(collection: Collection) {
        collectionsDao.saveCollection(collection)
        this.saveMoviesInCollection(collection)
    }

    fun isCollectionInDatabase(type: CollectionType): Single<Int> {
        return collectionsDao.isCollectionInDatabase(type.name)
    }
}