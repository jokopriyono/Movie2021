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

        // ====Run left rotation=======================================
        val arr = listOf(2, 3, 5, 1, 2, 3, 9, 8)
        val numOfRotation = 5
        val resultLeftRotation = leftRotation(arr, numOfRotation)
        println(resultLeftRotation)
    }

    private fun leftRotation(arr: List<Int>, numOfRotation: Int): List<Int> {
        val temp = arr.toMutableList()
        val cut = mutableListOf<Int>()
        for (i in 0 until numOfRotation) cut.add(temp.removeAt(0))
        temp.addAll(cut)
        return temp
    }
}