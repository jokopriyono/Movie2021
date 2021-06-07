package com.joko.movie2021.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.repository.collections.CollectionsRepository
import com.joko.movie2021.repository.movies.MoviesRepository
import com.joko.movie2021.ui.UIState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeoutException

class HomeViewModel(
    private val collectionsRepository: CollectionsRepository,
    private val moviesRepository: MoviesRepository,
    initialState: UIState.HomeScreenState
) : MVRxLiteViewModel<UIState.HomeScreenState>(initialState) {

    private val compositeDisposable = CompositeDisposable()
    private val _message = MutableLiveData<String>()

    val message: LiveData<String>
        get() = _message

    init {
        getPopularMovies()
        getUpcomingMovies()
    }

    private fun getPopularMovies() {
        collectionsRepository.getCollectionFlowable(type = CollectionType.Popular)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { popularMovies ->
                    setState { copy(popularMoviesResource = popularMovies) }
                },
                onError = { error -> handleError(error, "get-popular-movies") }
            )
            .disposeWith(compositeDisposable)
    }

    private fun getUpcomingMovies() {
        collectionsRepository.getCollectionFlowable(type = CollectionType.Upcoming)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { upcomingMovies ->
                    setState { copy(upcomingMoviesResource = upcomingMovies) }
                },
                onError = { error -> handleError(error, "get-upcoming-movies") }
            )
            .disposeWith(compositeDisposable)
    }

    fun forceRefreshCollection(collectionType: CollectionType) {
        collectionsRepository.forceRefreshCollection(type = collectionType)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
    }


    private fun handleError(error: Throwable, caller: String) {
        error.localizedMessage?.let { log("ERROR $caller -> $it") } ?: log("ERROR $caller ->")
            .also {
                error.printStackTrace()
            }
        when (error) {
            is IOException -> _message.postValue("Please check your internet connection")
            is TimeoutException -> _message.postValue("Request timed out")
            else -> _message.postValue("An error occurred")
        }
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}