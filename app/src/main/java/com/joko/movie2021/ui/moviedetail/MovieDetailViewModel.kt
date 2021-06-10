package com.joko.movie2021.ui.moviedetail

import androidx.lifecycle.LiveData
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.actors.Actor
import com.joko.movie2021.repository.collections.Collection
import com.joko.movie2021.repository.collections.CollectionType
import com.joko.movie2021.repository.collections.CollectionsRepository
import com.joko.movie2021.repository.movies.Cast
import com.joko.movie2021.repository.movies.Movie
import com.joko.movie2021.repository.movies.MoviesRepository
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.utils.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.io.IOException
import java.util.concurrent.TimeoutException

class MovieDetailViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
    private val collectionsRepository: CollectionsRepository,
    initialState: UIState.DetailsScreenState
) : MVRxLiteViewModel<UIState.DetailsScreenState>(initialState) {

    private val compositeDisposable = CompositeDisposable()
    private val _message = SingleLiveEvent<String>()

    val message: LiveData<String> get() = _message

    fun getAllMovieInfo() {
        this.getMovieDetails()
            .observeOn(Schedulers.computation())
            .doOnNext { movie ->
                setState { copy(movieResource = movie) }
            }
            .skip(1) // We skip the first value because it is Resource.Loading
            .publish()
            .apply {
                switchMap { getMovieCast() }
                    .switchMapSingle { castResource ->
                        if (castResource is Resource.Success) {
                            getMovieActors(castResource.data.castMembersIds)
                        } else {
                            getMovieActors(emptyList())
                        }
                    }
                    .subscribe(
                        { actorsList ->
                            setState { copy(castResource = actorsList) }
                        },
                        { error -> handleError(error, "get-movie-cast") }
                    )
                    .disposeWith(compositeDisposable)
            }
            .connect()
            .disposeWith(compositeDisposable)
    }

    fun isMovieFavorite() {
        withState { state ->
            Observable.just(collectionsRepository.getFavoriteCollection())
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .subscribe { result ->
                    val coll = result.blockingGet()
                    val find = coll.contents.filter { it == state.movieId }
                    setState {
                        copy(
                            favoriteMoviesResource = Resource.Success(coll),
                            isFavorite = find.isNotEmpty()
                        )
                    }
                }
                .disposeWith(compositeDisposable)
        }
    }

    fun toggleFavorite() {
        withState { state ->
            if (state.favoriteMoviesResource is Resource.Success) {
                val contents = mutableListOf<Int>()
                if (state.isFavorite) {
                    val movieRemoved = state.favoriteMoviesResource.data.contents
                        .filter { it != state.movieId }
                    contents.addAll(movieRemoved)
                } else {
                    contents.addAll(state.favoriteMoviesResource.data.contents)
                    contents.add(state.movieId)
                }
                val newCollection = Collection(CollectionType.Favourite.name, contents)
                Observable
                    .just(collectionsRepository.updateFavoritesCollection(newCollection))
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .subscribe({
                        isMovieFavorite()
                    }, {})
                    .disposeWith(compositeDisposable)
            }
        }
    }

    private fun getMovieDetails(): Observable<Resource<Movie>> {
        return moviesRepository.getMovieDetailsFlowable(movieId)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
    }

    private fun getMovieCast(): Observable<Resource<Cast>> {
        return moviesRepository.getMovieCast(movieId)
            .init(compositeDisposable)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
    }

    private fun getMovieActors(ids: List<Int>, count: Int = 8): Single<List<Resource<Actor>>> {
        return moviesRepository.getActorsInMovie(ids.take(count))
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.computation())
    }

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }

    private fun handleError(error: Throwable, caller: String) {
        error.localizedMessage?.let {
            log("ERROR $caller -> $it")
        } ?: log("ERROR $caller ->")
            .also {
                error.printStackTrace()
            }
        when (error) {
            is IOException -> _message.postValue("Please check your internet connection")
            is TimeoutException -> _message.postValue("Request timed out")
            else -> _message.postValue("An error occurred")
        }
    }
}
