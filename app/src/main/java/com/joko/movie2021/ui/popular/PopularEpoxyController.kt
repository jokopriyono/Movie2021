package com.joko.movie2021.ui.popular

import android.os.Handler
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.ui.common.movieGrid

class PopularEpoxyController(
    private val callbacks: EpoxyCallbacks,
    private val glide: RequestManager,
    epoxyHandler: Handler
) : TypedEpoxyController<UIState.PopularScreenState>(epoxyHandler, epoxyHandler) {

    override fun buildModels(state: UIState.PopularScreenState) {
        with(state) {
            when (popularMoviesResource) {
                is Resource.Success -> {
                    popularMoviesResource.data.forEach { movie ->
                        movieGrid {
                            id(movie.id)
                            genres(movie.genres ?: listOf())
                            title(movie.title)
                            overview(movie.overview)
                            posterUrl(movie.posterPath)
                            glide(glide)
                            transitionName("poster-${movie.id}")
                            clickListener { _, _, clickedView, _ ->
                                callbacks.onMovieItemClicked(
                                    movie.id,
                                    "poster-${movie.id}",
                                    clickedView
                                )
                            }
                        }
                    }
                }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }.safe
        }
    }
}