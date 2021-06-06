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
                buildHomeModel(popularMoviesResource, topRatedMoviesResource)
            }
        }
    }

    private fun buildHomeModel(
        popularMovies: Resource<List<Movie>>?,
        topRatedMovies: Resource<List<Movie>>?
    ) {
        when (popularMovies) {
            is Resource.Success -> {
                popularMovies.data.forEach { popularMovie ->
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
//
//        header {
//            id("top-rated")
//            title("Top Rated")
//            spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
//        }
//
//        when (topRatedMovies) {
//            is Resource.Success -> {
//                topRatedMovies.data.forEach { topRatedMovie ->
//                    movie {
//                        id(topRatedMovie.id)
//                        movieId(topRatedMovie.id)
//                        glide(glide)
//                        posterUrl(topRatedMovie.posterPath)
//                        transitionName("poster-${topRatedMovie.id}")
//                        clickListener { model, _, clickedView, _ ->
//                            callbacks.onMovieItemClicked(model.movieId!!, model.transitionName(), clickedView)
//                        }
//                    }
//                }
//            }
//            is Resource.Error -> {
//                infoText {
//                    id("error-top-rated-movies")
//                    text("Error getting Top Rated movies")
//                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
//                }
//            }
//            is Resource.Loading -> {
//                loading {
//                    id("load-top-rated-movies")
//                    description("Loading Top Rated movies")
//                    spanSizeOverride { totalSpanCount, _, _ -> totalSpanCount }
//                }
//            }
//            null -> Unit
//        }.safe
    }
}