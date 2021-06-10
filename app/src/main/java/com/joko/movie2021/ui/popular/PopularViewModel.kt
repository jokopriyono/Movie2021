package com.joko.movie2021.ui.popular

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.repository.collections.CollectionsRepository
import com.joko.movie2021.ui.UIState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeoutException

class PopularViewModel(
    private val collectionsRepository: CollectionsRepository,
    initialState: UIState.PopularScreenState
) : MVRxLiteViewModel<UIState.PopularScreenState>(initialState) {
    private val compositeDisposable = CompositeDisposable()
    private val _message = MutableLiveData<String>()

    val message: LiveData<String>
        get() = _message

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    fun getPopularMovies() {
        withState { state ->
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
    }

    fun forceRefreshPopularCollection() {
        withState { state ->
            collectionsRepository.forceRefreshCollection(type = CollectionType.Popular)
                .init(compositeDisposable)
                .subscribeOn(Schedulers.io())
        }
    }

    private fun handleError(error: Throwable, caller: String) {
        error.localizedMessage?.let { log("ERROR $caller -> $it") }
            ?: log("ERROR $caller ->").also { error.printStackTrace() }
        when (error) {
            is IOException -> _message.postValue("Please check your internet connection")
            is TimeoutException -> _message.postValue("Request timed out")
            else -> _message.postValue("An error occurred")
        }
    }
}