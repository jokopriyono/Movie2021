package com.joko.movie2021.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.joko.movie2021.R
import com.joko.movie2021.core.Resource
import com.joko.movie2021.mvrxlite.MVRxLiteView
import com.joko.movie2021.ui.BaseFragment
import com.joko.movie2021.ui.UIState
import kotlinx.android.synthetic.main.fragment_favorite.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit

class FavoriteFragment : BaseFragment(), MVRxLiteView<UIState.FavoriteScreenState> {

    private val callbacks = object : FavoriteEpoxyController.MovieDetailsCallbacks {
        override fun onDeleteItemClicked(id: Int, transitionName: String, sharedView: View?) {
            favoriteViewModel.deleteFavoriteMovie(id)
        }

        override fun onMovieItemClicked(id: Int, transitionName: String, sharedView: View?) {
            val action = FavoriteFragmentDirections.viewMovieDetails(
                movieIdArg = id,
                transitionNameArg = transitionName
            )
            sharedView?.let {
                val extras = FragmentNavigatorExtras(sharedView to transitionName)
                findNavController().navigate(action, extras)
            } ?: findNavController().navigate(action)
        }
    }

    private val glideRequestManager: RequestManager by inject(named("fragment-glide-request-manager")) {
        parametersOf(this)
    }

    private val favoriteEpoxyController: FavoriteEpoxyController by inject {
        parametersOf(callbacks, glideRequestManager)
    }

    private val onDestroyView: PublishRelay<Unit> = PublishRelay.create()

    override val initialState: UIState by lazy {
        UIState.FavoriteScreenState(favoriteMoviesResource = Resource.Loading(), "")
    }

    private val favoriteViewModel: FavoriteViewModel by viewModel {
        parametersOf(initialState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        favoriteViewModel.apply {
            state.observe(viewLifecycleOwner, { state ->
                renderState(state)
            })
            getFavoriteMovies()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_favorite.apply {
            layoutManager = LinearLayoutManager(context)
            setController(favoriteEpoxyController)
        }
        setupSearchBox()
        // To make sure the search box does not have focus
        getView()?.requestFocus()
    }

    private fun setupSearchBox() {
        edt_search.textChanges()
            .debounce(300, TimeUnit.MILLISECONDS)
            .map { text -> text.toString() }
            .takeUntil(onDestroyView)
            .doOnNext { query ->
                favoriteViewModel.getSearchResultsForQuery(query)
            }
            .subscribe()
    }

    override fun renderState(state: UIState.FavoriteScreenState) {
        favoriteEpoxyController.setData(state)
    }
}