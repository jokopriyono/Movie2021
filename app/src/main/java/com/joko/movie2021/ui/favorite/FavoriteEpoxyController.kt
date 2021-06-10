package com.joko.movie2021.ui.favorite

import android.os.Handler
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.format
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.ui.common.favorite

class FavoriteEpoxyController(
    private val callbacks: EpoxyCallbacks,
    private val glide: RequestManager,
    epoxyHandler: Handler
) : TypedEpoxyController<UIState.FavoriteScreenState>(epoxyHandler, epoxyHandler) {

    override fun buildModels(state: UIState.FavoriteScreenState) {
        with(state) {
            when (favoriteMoviesResource) {
                is Resource.Success -> {
                    favoriteMoviesResource.data.forEach { movie ->
                        favorite {
                            id(movie.id)
                            genres(movie.genres ?: listOf())
                            title(movie.title)
                            year(movie.releaseDate.format("yyyy"))
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