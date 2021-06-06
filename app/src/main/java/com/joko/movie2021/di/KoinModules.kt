package com.joko.movie2021.di

import android.content.Context
import android.content.SharedPreferences
import android.os.Handler
import android.os.HandlerThread
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.joko.movie2021.R
import com.joko.movie2021.ui.UIState
import com.joko.movie2021.ui.common.EpoxyCallbacks
import com.joko.movie2021.ui.main.HomeEpoxyController
import com.joko.movie2021.ui.main.HomeViewModel
import com.joko.movie2021.ui.main.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val applicationModule = module {
    single<SharedPreferences> {
        androidContext().getSharedPreferences(
            androidContext().getString(R.string.app_name),
            Context.MODE_PRIVATE
        )
    }
}

val uiModule = module {

    viewModel { (initialState: UIState.HomeScreenState) ->
        HomeViewModel(get(), get(), initialState)
    }

    viewModel { MainViewModel(get()) }

    factory(named("fragment-glide-request-manager")) { (fragment: Fragment) ->
        Glide.with(fragment)
    }

    factory(named("view-glide-request-manager")) { (view: View) ->
        Glide.with(view)
    }

    factory { (callbacks: EpoxyCallbacks, glide: RequestManager) ->
        HomeEpoxyController(callbacks, glide, get(named("epoxy-handler")))
    }

    single(named("epoxy-handler-thread")) {
        HandlerThread("epoxy").apply {
            start()
        }
    }

    single(named("epoxy-handler")) {
        val handlerThread = get<HandlerThread>(named("epoxy-handler-thread"))
        Handler(handlerThread.looper)
    }
}


