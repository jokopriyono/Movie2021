package com.joko.movie2021.ui.main

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxrelay2.PublishRelay
import com.joko.movie2021.R
import com.joko.movie2021.core.Resource
import com.joko.movie2021.mvrxlite.MVRxLiteView
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.ui.BaseFragment
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.BackPressListener
import com.joko.movie2021.ui.common.EpoxyCallbacks
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named

class HomeFragment :
    BaseFragment(),
    BackPressListener,
    MVRxLiteView<UIState.HomeScreenState> {

    private val mainViewModel: MainViewModel by sharedViewModel()

    private val callbacks = object : EpoxyCallbacks {
        override fun onMovieItemClicked(id: Int, transitionName: String, sharedView: View?) {
            // TODO item clicked
            val action = HomeFragmentDirections.viewMovieDetails(
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

    private val homeEpoxyController: HomeEpoxyController by inject {
        parametersOf(callbacks, glideRequestManager)
    }

    private val onDestroyView: PublishRelay<Unit> = PublishRelay.create()

    override val initialState: UIState by lazy {
        UIState.HomeScreenState(
            popularMoviesResource = Resource.Loading(),
            topRatedMoviesResource = Resource.Loading(),
            searchResultsResource = null
        )
    }

    private val homeViewModel: HomeViewModel by viewModel {
        parametersOf(initialState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        homeViewModel.apply {

            message.observe(viewLifecycleOwner, Observer { message ->
                view?.let { Snackbar.make(it, message, Snackbar.LENGTH_SHORT).show() }
            })

            state.observe(viewLifecycleOwner, Observer { state ->
                renderState(state)
            })

            forceRefreshCollection(CollectionType.Popular)
            forceRefreshCollection(CollectionType.TopRated)
        }
        mainViewModel.setBackPressListener(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val transition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition.apply {
            duration = 500
        }

        sharedElementReturnTransition = transition.apply {
            duration = 500
        }

        postponeEnterTransition()
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recycler_popular.apply {
            setController(homeEpoxyController)
        }
        (view.parent as ViewGroup).doOnPreDraw {
            startPostponedEnterTransition()
        }
    }

    override fun onBackPressed(): Boolean {
        return with(homeViewModel) {
            state.value!!.searchResultsResource?.let {
                this@HomeFragment.view?.requestFocus()
                false
            } ?: true
        }
    }

    override fun renderState(state: UIState.HomeScreenState) {
        homeEpoxyController.setData(state)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        onDestroyView.accept(Unit)
        mainViewModel.setBackPressListener(null)
    }
}