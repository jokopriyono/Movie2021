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

        // ====Run valid string========================================
        val str1 = "aabbccddeefghi"
        print("input: $str1 -> result: ${validString(str1)}")
        val str2 = "abcdefghhgfedecba"
        print("input: $str2 -> result: ${validString(str2)}")
        val str3 = "abcbdcd"
        print("input: $str3 -> result: ${validString(str3)}")

    }

    private fun leftRotation(arr: List<Int>, numOfRotation: Int): List<Int> {
        val temp = arr.toMutableList()
        val cut = mutableListOf<Int>()
        for (i in 0 until numOfRotation) cut.add(temp.removeAt(0))
        temp.addAll(cut)
        return temp
    }

    private fun validString(input: String): Boolean {
        data class AboutString(val char: String, var count: Int)

        val listString = mutableListOf<AboutString>()
        input.toCharArray().map { it.toString() }.toTypedArray().forEach {
            val find = listString.find { obj -> obj.char == it }
            if (find != null) find.count++
            else listString.add(AboutString(it, 1))
        }
        val reduceCount = mutableSetOf<Int>()
        listString.forEach { reduceCount.add(it.count) }
        if (reduceCount.size > 2) return false
        else if (reduceCount.size > 1) {
            listString.forEachIndexed { idx, obj ->
                val newObj = AboutString(obj.char, obj.count - 1)
                val filter = mutableListOf<AboutString>()
                filter.addAll(listString)
                filter.removeAt(idx)
                if (newObj.count > 0) filter.add(idx, newObj)
                reduceCount.clear()
                filter.forEach { reduceCount.add(it.count) }
                if (reduceCount.size == 1) return true
            }
            return false
        }
        return true
    }
}