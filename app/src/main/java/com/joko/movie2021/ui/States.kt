package com.joko.movie2021.ui

import android.view.View
import com.joko.movie2021.core.Resource
import com.joko.movie2021.mvrxlite.MVRxLiteState
import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.collections.Collection
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.repository.movies.MovieTrailer

sealed class UIState : MVRxLiteState {

    data class HomeScreenState(
        val popularMoviesResource: Resource<List<Movie>>,
        val upcomingMoviesResource: Resource<List<Movie>>,
        val topRatedMoviesResource: Resource<List<Movie>>,
        val nowPlayingMoviesResource: Resource<List<Movie>>,
        val searchResultsResource: Resource<List<Movie>>?
    ) : UIState()

    data class DetailsScreenState(
        val movieId: Int = -1,
        val transitionName: String? = null,
        val sharedView: View? = null,
        val movieResource: Resource<Movie>,
        val trailerResource: Resource<MovieTrailer>,
        val castResource: List<Resource<Actor>>,
        val favoriteMoviesResource: Resource<Collection>,
        val isFavorite: Boolean = false
    ) : UIState()

    data class PopularScreenState(
        val popularMoviesResource: Resource<List<Movie>>,
        var lastQuery: String?
    ) : UIState()

    data class FavoriteScreenState(
        val favoriteMoviesResource: Resource<List<Movie>>,
        var lastQuery: String?
    ) : UIState()
}