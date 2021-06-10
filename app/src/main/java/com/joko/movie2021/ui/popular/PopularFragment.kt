package com.joko.movie2021.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.jakewharton.rxbinding3.widget.textChanges
import com.jakewharton.rxrelay2.PublishRelay
import com.joko.movie2021.R
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.gone
import com.joko.movie2021.core.extensions.visible
import com.joko.movie2021.mvrxlite.MVRxLiteView
import com.joko.movie2021.ui.BaseFragment
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.utils.EqualSpaceGridItemDecoration
import kotlinx.android.synthetic.main.fragment_popular.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class PopularFragment : BaseFragment(), MVRxLiteView<UIState.PopularScreenState> {

    private val callbacks = object : EpoxyCallbacks {
        override fun onMovieItemClicked(id: Int, transitionName: String, sharedView: View?) {
            val action = PopularFragmentDirections.viewMovieDetails(
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

    private val popularEpoxyController: PopularEpoxyController by inject {
        parametersOf(callbacks, glideRequestManager)
    }

    private val onDestroyView: PublishRelay<Unit> = PublishRelay.create()

    override val initialState: UIState by lazy {
        UIState.PopularScreenState(popularMoviesResource = Resource.Loading(), null)
    }

    private val popularViewModel: PopularViewModel by viewModel {
        parametersOf(initialState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        popularViewModel.apply {
            getPopularMovies()
            state.observe(viewLifecycleOwner, { state ->
                renderState(state)
            })

            forceRefreshPopularCollection()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_popular, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_popular.apply {
            val space = resources.getDimension(R.dimen.movie_grid_item_space)
            layoutManager = GridLayoutManager(context, 2)
            addItemDecoration(EqualSpaceGridItemDecoration(space.roundToInt()))
            setController(popularEpoxyController)
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
                popularViewModel.getSearchResultsForQuery(query)
            }
            .subscribe()
    }

    override fun renderState(state: UIState.PopularScreenState) {
        if (state.lastQuery != null) {
            txt_result.visible()
            val txt = "Showing result of ‘${state.lastQuery}’"
            txt_result.text = txt
        } else {
            txt_result.gone()
        }
        popularEpoxyController.setData(state)
    }
}