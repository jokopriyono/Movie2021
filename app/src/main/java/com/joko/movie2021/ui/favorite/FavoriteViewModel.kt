package com.joko.movie2021.ui.favorite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.collections.Collection
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.repository.collections.CollectionsRepository
import com.joko.movie2021.ui.UIState
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeoutException

class FavoriteViewModel(
    private val collectionsRepository: CollectionsRepository,
    initialState: UIState.FavoriteScreenState
) : MVRxLiteViewModel<UIState.FavoriteScreenState>(initialState) {
    private val compositeDisposable = CompositeDisposable()
    private var currentQuery: Disposable? = null
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

    fun deleteFavoriteMovie(movieId: Int) {
        withState { state ->
            if (state.favoriteMoviesResource is Resource.Success) {
                Observable.just(collectionsRepository.getFavoriteCollection())
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe({ coll ->
                        val movies = coll.blockingGet()
                        val contents = mutableListOf<Int>()
                        val movieRemoved = movies.contents.filter { it != movieId }
                        contents.addAll(movieRemoved)
                        val newCollection = Collection(CollectionType.Favourite.name, contents)
                        Observable
                            .just(collectionsRepository.updateFavoritesCollection(newCollection))
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.computation())
                            .subscribe({
                                getFavoriteMovies()
                            }, {})
                            .disposeWith(compositeDisposable)
                    }, {})
                    .disposeWith(compositeDisposable)
            }
        }
    }

    fun getSearchResultsForQuery(query: String) {
        currentQuery?.dispose()
        currentQuery = collectionsRepository.searchFavoriteCollectionFlowable(query)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { searchResults ->
                    setState {
                        copy(favoriteMoviesResource = searchResults, lastQuery = query)
                    }
                },
                { error ->
                    handleError(error, "get-search-results")
                }
            )
        currentQuery?.disposeWith(compositeDisposable)
    }
}