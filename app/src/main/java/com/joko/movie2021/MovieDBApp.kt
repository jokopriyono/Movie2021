package com.joko.movie2021

import android.app.Application

class MovieDBApp : Application() {
    override fun onCreate() {
        super.onCreate()
//        startKoin {
//            androidContext(this@MovieDBApp.applicationContext)
//            modules(
//                listOf(
//                    applicationModule,
//                    retrofitModule,
//                    apiModule,
//                    uiModule,
//                    databaseModule,
//                    repositoryModule
//                )
//            )
//        }
    }
}