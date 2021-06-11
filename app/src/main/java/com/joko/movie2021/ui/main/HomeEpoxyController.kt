package com.joko.movie2021.ui.main

import android.os.Handler
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.ui.common.movie

class HomeEpoxyController(
    private val callbacks: EpoxyCallbacks,
    private val glide: RequestManager,
    private val type: CollectionType,
    epoxyHandler: Handler
) : TypedEpoxyController<UIState.HomeScreenState>(epoxyHandler, epoxyHandler) {

    override fun buildModels(state: UIState.HomeScreenState) {
        with(state) {
            when (type) {
                CollectionType.Popular -> buildHomeModel(popularMoviesResource)
                CollectionType.Upcoming -> buildHomeModel(upcomingMoviesResource)
                CollectionType.TopRated -> buildHomeModel(topRatedMoviesResource)
                CollectionType.NowPlaying -> buildHomeModel(nowPlayingMoviesResource)
                else -> Unit
            }
        }
    }

    private fun buildHomeModel(movies: Resource<List<Movie>>?) {
        when (movies) {
            is Resource.Success -> {
                val showTenOnly = movies.data.subList(0, 10)
                showTenOnly.forEach { popularMovie ->
                    movie {
                        id(popularMovie.id)
                        movieId(popularMovie.id)
                        glide(glide)
                        posterUrl(popularMovie.posterPath)
                        transitionName("poster-${popularMovie.id}")
                        clickListener { model, _, clickedView, _ ->
                            callbacks.onMovieItemClicked(
                                model.movieId!!,
                                model.transitionName(),
                                clickedView
                            )
                        }
                    }
                }
            }
            is Resource.Error -> {
                // TODO error condition
            }
            is Resource.Loading -> {
                // TODO loading state
            }
            null -> Unit
        }.safe
    }
}