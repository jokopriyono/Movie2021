package com.joko.movie2021.ui

import com.joko.movie2021.core.Resource
import com.joko.movie2021.mvrxlite.MVRxLiteState
import com.joko.movie2021.repository.movies.Movie

sealed class UIState : MVRxLiteState {

    data class HomeScreenState(
        val popularMoviesResource: Resource<List<Movie>>,
        val topRatedMoviesResource: Resource<List<Movie>>,
        val searchResultsResource: Resource<List<Movie>>?
    ) : UIState()
}