package com.joko.movie2021.ui.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.BackPressListener
import com.joko.movie2021.utils.SingleLiveEvent

class MainViewModel(sharedPreferences: SharedPreferences) : ViewModel() {

    private val _state = SingleLiveEvent<UIState>()
    private var _backPressListener = MutableLiveData<BackPressListener>()

    val state: LiveData<UIState>
        get() = _state

    val backPressListener: LiveData<BackPressListener>
        get() = _backPressListener

    fun setBackPressListener(listener: BackPressListener?) = _backPressListener.postValue(listener)
}