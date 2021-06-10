package com.joko.movie2021.ui.moviedetail

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.doOnPreDraw
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.snackbar.Snackbar
import com.joko.movie2021.R
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.gone
import com.joko.movie2021.core.extensions.safe
import com.joko.movie2021.mvrxlite.MVRxLiteView
import com.joko.movie2021.ui.BaseFragment
import com.joko.movie2021.ui.UIState
import kotlinx.android.synthetic.main.fragment_movie_details.*
import kotlinx.android.synthetic.main.fragment_movie_details.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class MovieDetailFragment : BaseFragment(), MVRxLiteView<UIState.DetailsScreenState> {

    private val safeArgs: MovieDetailFragmentArgs by navArgs()

    private val callbacks = object : MovieDetailEpoxyController.MovieDetailsCallbacks {
        override fun onActorItemClicked(id: Int, transitionName: String, sharedView: View?) {
            // TODO actor clicked
        }
    }

    private val glideRequestManager: RequestManager by inject(named("fragment-glide-request-manager")) {
        parametersOf(this)
    }

    private val detailsEpoxyController: MovieDetailEpoxyController by inject {
        parametersOf(callbacks, glideRequestManager)
    }

    override val initialState: UIState by lazy {
        UIState.DetailsScreenState(
            movieId = safeArgs.movieIdArg,
            movieResource = Resource.Loading(),
            trailerResource = Resource.Loading(),
            castResource = listOf(Resource.Loading()),
            favoriteMoviesResource = Resource.Loading()
        )
    }

    private val movieDetailsViewModel: MovieDetailViewModel by viewModel {
        parametersOf(safeArgs.movieIdArg, initialState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        val animatorDuration =
            requireContext().resources.getInteger(R.integer.sharedElementTransitionDuration)
                .toLong()

        sharedElementEnterTransition = transition.apply {
            duration = animatorDuration
        }

        sharedElementReturnTransition = transition.apply {
            duration = animatorDuration
        }

        postponeEnterTransition()
        return inflater
            .inflate(R.layout.fragment_movie_details, container, false)
            .apply {
                ViewCompat.setTransitionName(this.img_movie, safeArgs.transitionNameArg)
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(movieDetailsViewModel) {
            isMovieFavorite()
            getAllMovieInfo()
            message.observe(viewLifecycleOwner, { message ->
                view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            })
            state.observe(viewLifecycleOwner, { state ->
                renderState(state)
            })
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_cast.apply {
            setController(detailsEpoxyController)
        }
        (view.parent as ViewGroup).doOnPreDraw {
            startPostponedEnterTransition()
        }
        img_back.setOnClickListener { activity?.onBackPressed() }
        btn_add_remove_favorite.setOnClickListener {
            movieDetailsViewModel.toggleFavorite()
        }
    }

    override fun renderState(state: UIState.DetailsScreenState) {
        detailsEpoxyController.setData(state)

        if (state.isFavorite) {
            btn_add_remove_favorite.setText(R.string.favorite_movie)
            btn_add_remove_favorite.setIconResource(R.drawable.ic_love)
        } else {
            btn_add_remove_favorite.setText(R.string.add_to_favorite)
            btn_add_remove_favorite.setIconResource(R.drawable.ic_add)
        }

        when (val movieResource = state.movieResource) {
            is Resource.Success -> {
                val movie = movieResource.data
                glideRequestManager
                    .load(movie.posterPath)
                    .apply {
                        RequestOptions()
//                            .placeholder(R.drawable.ic_round_local_movies_24px)
//                            .error(R.drawable.ic_round_local_movies_24px)
//                            .fallback(R.drawable.ic_round_local_movies_24px)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                    }
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            startPostponedEnterTransition()
                            return false
                        }
                    })
                    .into(img_movie)

                txt_title.text = movie.title
                linear_genre.removeAllViews()
                movie.genres?.mapIndexed { idx, text ->
                    val tv = TextView(context)
                    tv.text = text
                    linear_genre.addView(tv)
                    if (idx != movie.genres!!.size - 1) {
                        val dot = ImageView(context)
                        dot.setImageResource(R.drawable.ic_dot)
                        dot.setPadding(5, 0, 5, 0)
                        linear_genre.addView(dot)
                    }
                }
                txt_synopsis.text = movie.overview
                if (movie.duration != null) {
                    val h = (movie.duration!! / 60)
                    val m = (movie.duration!! % 60)
                    val text = "${h}h ${m}m"
                    txt_duration.text = text
                } else {
                    txt_duration.gone()
                }
            }
            else -> Unit
        }.safe
    }
}