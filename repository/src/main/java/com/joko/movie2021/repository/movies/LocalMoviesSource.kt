package com.joko.movie2021.repository.movies

import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.repository.actors.ActorsDao

internal class LocalMoviesSource(
    private val moviesDao: MovieDao,
    private val actorsDao: ActorsDao
) {

    fun getMovieFlowable(id: Int) = moviesDao.getMovieFlowable(id)

    fun getAccountStateForMovieFlowable(movieId: Int) =
        moviesDao.getAccountStatesForMovieFlowable(movieId)

    fun getCastForMovieFlowable(movieId: Int) = moviesDao.getCastForMovieFlowable(movieId)

    fun getMovieTrailerFlowable(movieId: Int) = moviesDao.getTrailerForMovieFlowable(movieId)

    fun getMovie(id: Int) = moviesDao.getMovie(id)

    fun getAccountStatesForMovie(movieId: Int) = moviesDao.getAccountStatesForMovie(movieId)

    fun isMovieInDatabase(id: Int) = moviesDao.isMovieInDatabase(id)

    fun isAccountStateInDatabase(movieId: Int) = moviesDao.isAccountStateInDatabase(movieId)

    fun isCastInDatabase(movieId: Int) = moviesDao.isCastInDatabase(movieId)

    fun isMovieTrailerInDatabase(movieId: Int) = moviesDao.isMovieTrailerInDatabase(movieId)

    fun saveMovieToDatabase(movie: Movie) = moviesDao.saveMovie(movie)

    fun saveAccountStateToDatabase(accountState: AccountState) =
        moviesDao.saveAccountState(accountState)

    fun saveCastToDatabase(cast: Cast) {
        log("Saving cast to database: $cast")
        moviesDao.saveCast(cast)
        cast.castMembers?.let {
            log("Saving cast actors to database: $it")
            actorsDao.saveAllActors(it)
        }
    }

    fun saveMovieTrailerToDatabase(movieTrailer: MovieTrailer) =
        moviesDao.saveMovieTrailer(movieTrailer)

    fun saveMoviesToDatabase(movies: List<Movie>) = moviesDao.saveAllMovies(movies)

    fun updateAccountStatesInDatabase(accountState: AccountState) =
        moviesDao.updateAccountState(accountState)

    fun getActorsForMovie(actorIds: List<Int>) = moviesDao.getActorsForMovie(actorIds)

}

