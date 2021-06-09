package com.joko.movie2021.ui.moviedetail

import android.os.Handler
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.cast

class MovieDetailEpoxyController(
    private val callbacks: MovieDetailsCallbacks,
    private val glide: RequestManager,
    epoxyHandler: Handler
) : TypedEpoxyController<UIState.DetailsScreenState>(epoxyHandler, epoxyHandler) {

    interface MovieDetailsCallbacks {
        fun onActorItemClicked(id: Int, transitionName: String, sharedView: View?)
    }

    override fun buildModels(state: UIState.DetailsScreenState) {
        with(state) {
            buildMovieResource(movieResource)
            buildCastResource(castResource)
        }
    }

    private fun buildMovieResource(resource: Resource<Movie>) {
        when (resource) {
            is Resource.Success -> {
                // TODO success state
            }
            is Resource.Error -> {
                // TODO error condition
            }
            is Resource.Loading -> {
                // TODO loading state
            }
        }.safe
    }

    private fun buildCastResource(actors: List<Resource<Actor>>) {
        actors.filterIsInstance<Resource.Success<Actor>>().takeIf { it.isNotEmpty() }
            ?.let { actorsList ->
                actorsList.forEach { actor ->
                    cast {
                        id(actor.data.id)
                        actorId(actor.data.id)
                        name(actor.data.name)
                        glide(glide)
                        pictureUrl(actor.data.profilePictureUrl)
                        transitionName("actor-${actor.data.id}")
                        clickListener { model, _, clickedView, _ ->
                            callbacks.onActorItemClicked(
                                model.actorId!!,
                                model.transitionName(),
                                clickedView
                            )
                        }
                    }

                }
            }
    }
}