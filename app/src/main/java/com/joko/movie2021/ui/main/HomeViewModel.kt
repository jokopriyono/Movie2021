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
import io.reactivex.Observable
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
        getMovies(CollectionType.Popular)
        getMovies(CollectionType.Upcoming)
        getMovies(CollectionType.TopRated)
        getMovies(CollectionType.NowPlaying)
    }

    fun checkFavoriteMovies() {
        Observable.just(collectionsRepository.getFavoriteCollection())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
            .subscribe({}, {
                Observable.just(collectionsRepository.insertEmptyFavoriteCollection())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe()
                    .disposeWith(compositeDisposable)
            })
            .disposeWith(compositeDisposable)
    }

    private fun getMovies(collectionType: CollectionType) {
        collectionsRepository.getCollectionFlowable(type = collectionType)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { movies ->
                    when (collectionType) {
                        CollectionType.Popular -> {
                            setState { copy(popularMoviesResource = movies) }
                        }
                        CollectionType.Upcoming -> {
                            setState { copy(upcomingMoviesResource = movies) }
                        }
                        CollectionType.TopRated -> {
                            setState { copy(topRatedMoviesResource = movies) }
                        }
                        CollectionType.NowPlaying -> {
                            checkFavoriteMovies()
                            setState { copy(nowPlayingMoviesResource = movies) }
                        }
                        else -> Unit
                    }
                },
                onError = { error -> handleError(error, "get-${collectionType.name}-movies") }
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