package com.joko.movie2021.ui.main

import android.os.Handler
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.ui.common.movie

class HomeEpoxyController(
    private val callbacks: EpoxyCallbacks,
    private val glide: RequestManager,
    epoxyHandler: Handler
) : TypedEpoxyController<UIState.HomeScreenState>(epoxyHandler, epoxyHandler) {

    override fun buildModels(state: UIState.HomeScreenState) {
        with(state) {
            run {
                buildHomeModel(popularMoviesResource, upcomingMoviesResource)
            }
        }
    }

    private fun buildHomeModel(
        popularMovies: Resource<List<Movie>>?,
        upcomingMovies: Resource<List<Movie>>?
    ) {
        when (popularMovies) {
            is Resource.Success -> {
                val showTenOnly = popularMovies.data.subList(0, 10)
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
        when (upcomingMovies) {
            is Resource.Success -> {
                val showTenOnly = upcomingMovies.data.subList(0, 10)
                showTenOnly.forEach { upcomingMovie ->
                    movie {
                        id(upcomingMovie.id)
                        movieId(upcomingMovie.id)
                        glide(glide)
                        posterUrl(upcomingMovie.posterPath)
                        transitionName("poster-${upcomingMovie.id}")
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