package com.joko.movie2021.ui.moviedetail

import android.os.Handler
import android.view.View
import com.airbnb.epoxy.*
import com.bumptech.glide.RequestManager
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.CastModel_

class MovieDetailEpoxyController(
    private val callbacks: MovieDetailEpoxyController.MovieDetailsCallbacks,
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
        actors
            .filterIsInstance<Resource.Success<Actor>>()
            .takeIf { it.isNotEmpty() }
            ?.let { actorsList ->
                carousel {
                    id("cast-carousel")
                    numViewsToShowOnScreen(
                        this@MovieDetailEpoxyController.spanCount.toFloat().minus(1f).times(1.2f)
                    )
                    withModelsFrom(actorsList) { actorResource ->
                        CastModel_()
                            .id(actorResource.data.id)
                            .actorId(actorResource.data.id)
                            .name(actorResource.data.name)
                            .glide(glide)
                            .pictureUrl(actorResource.data.profilePictureUrl)
                            .transitionName("actor-${actorResource.data.id}")
                            .clickListener { model, _, clickedView, _ ->
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

    /** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
     *
     */
    private inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
        CarouselModel_().apply {
            modelInitializer()
        }.addTo(this)
    }

    /** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
     *
     * @param items The items to transform to models
     * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
     */
    private inline fun <T> CarouselModelBuilder.withModelsFrom(
        items: List<T>,
        modelBuilder: (T) -> EpoxyModel<*>
    ) {
        models(items.map { modelBuilder(it) })
    }
}