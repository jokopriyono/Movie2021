package com.joko.movie2021.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.collections.CollectionsRepository
import com.joko.movie2021.ui.UIState
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeoutException

class FavoriteViewModel(
    private val collectionsRepository: CollectionsRepository,
    initialState: UIState.FavoriteScreenState
) : MVRxLiteViewModel<UIState.FavoriteScreenState>(initialState) {
    private val compositeDisposable = CompositeDisposable()
    private val _message = MutableLiveData<String>()

    val message: LiveData<String>
        get() = _message

    fun getFavoriteMovies() {
        collectionsRepository.getFavoriteCollectionFlowable()
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .subscribeBy(
                onNext = { favoriteMovies ->
                    setState { copy(favoriteMoviesResource = favoriteMovies) }
                },
                onError = { error -> handleError(error, "get-popular-movies") }
            )
            .disposeWith(compositeDisposable)
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

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}