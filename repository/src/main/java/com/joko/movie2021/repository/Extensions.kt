package com.joko.movie2021.repository

import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.data.remote.service.common.GeneralMovieResponse
import com.joko.movie2021.repository.data.remote.service.movie.CastMember
import com.joko.movie2021.repository.data.remote.service.movie.MovieResponse
import com.joko.movie2021.repository.data.remote.service.movie.MovieStatesResponse
import com.joko.movie2021.repository.data.remote.service.movie.MovieVideo
import com.joko.movie2021.repository.data.remote.service.people.PersonResponse
import com.joko.movie2021.repository.data.remote.utils.Config
import com.joko.movie2021.repository.movies.AccountState
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.repository.movies.MovieTrailer


internal fun GeneralMovieResponse.toMovie(): Movie {
    return Movie(
        this.id,
        this.title,
        this.posterPath.getPosterUrl(),
        this.backdropPath.getBackdropUrl(),
        this.overview,
        this.voteAverage,
        this.releaseDate,
        this.genreIds,
        this.isAdultMovie,
        null,
        null,
        null,
        false,
        null
    )
}

internal fun MovieResponse.toMovie(): Movie {
    return Movie(
        id = this.id,
        title = this.title,
        posterPath = this.posterPath.getPosterUrl(),
        backdropPath = this.backdropPath.getBackdropUrl(),
        overview = this.overview ?: "",
        voteAverage = this.voteAverage,
        releaseDate = this.releaseDate,
        genreIds = this.genres.map { genrePair -> genrePair.id },
        isAdult = this.isAdult,
        budget = this.budget,
        revenue = this.revenue,
        genres = this.genres.map { genrePair -> genrePair.name },
        isModelComplete = true,
        duration = this.runtime
    )
}

internal fun CastMember.toActor(): Actor {
    return Actor(
        this.id,
        this.profilePath.getProfilePictureUrl(),
        this.name,
        null,
        null,
        false
    )
}

internal fun PersonResponse.toActor(): Actor {
    return Actor(
        this.id,
        this.profilePath.getProfilePictureUrl(),
        this.name,
        this.birthday,
        this.biography,
        true
    )
}

internal fun MovieVideo.toMovieTrailer(movieId: Int): MovieTrailer {
    return MovieTrailer(
        movieId,
        this.key
    )
}

internal fun MovieStatesResponse.toAccountState(movieId: Int): AccountState {
    return AccountState(
        isWatchlisted = this.isWatchlisted ?: false,
        isFavourited = this.isFavourited ?: false,
        movieId = movieId
    )
}

internal fun String.getYoutubeUrl() = "https://www.youtube.com/watch?v=$this"

internal fun String?.getPosterUrl(): String {
    return this?.let {
        "${Config.BASE_IMAGE_URL}${Config.DEFAULT_POSTER_SIZE}$it"
    } ?: ""
}

internal fun String?.getBackdropUrl(): String {
    return this?.let {
        "${Config.BASE_IMAGE_URL}${Config.DEFAULT_BACKDROP_SIZE}${this}"
    } ?: ""
}

internal fun String?.getProfilePictureUrl(): String {
    return this?.let {
        "${Config.BASE_IMAGE_URL}${Config.SMALL_PROFILE_PICTURE_SIZE}$this"
    } ?: ""
}