package com.joko.movie2021.mvrxlite

interface MVRxLiteView<T : MVRxLiteState> {
    fun renderState(state: T)
}