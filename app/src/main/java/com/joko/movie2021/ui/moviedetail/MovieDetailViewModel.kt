package com.joko.movie2021.ui.moviedetail

import androidx.lifecycle.LiveData
import com.joko.movie2021.core.Resource
import com.joko.movie2021.core.extensions.disposeWith
import com.joko.movie2021.core.extensions.log
import com.joko.movie2021.mvrxlite.MVRxLiteViewModel
import com.joko.movie2021.repository.actors.Actor
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
    initialState: UIState.DetailsScreenState
) : MVRxLiteViewModel<UIState.DetailsScreenState>(initialState) {

    private val compositeDisposable = CompositeDisposable()
    private val _message = SingleLiveEvent<String>()

    val message: LiveData<String>
        get() = _message

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
