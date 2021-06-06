package com.joko.movie2021.ui.common

import android.view.View

interface EpoxyCallbacks {
    fun onMovieItemClicked(id: Int, transitionName: String = "", sharedView: View?)
}