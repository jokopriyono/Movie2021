package com.joko.movie2021.mvrxlite

import io.reactivex.Observable
import io.reactivex.disposables.Disposable

interface MVRxLiteStateStore<S : Any> : Disposable {
    val state: S
    fun get(block: (S) -> Unit)
    fun set(stateReducer: S.() -> S)
    val observable: Observable<S>
}
