package com.joko.movie2021.ui.main

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.joko.movie2021.R
import com.joko.movie2021.ui.common.BackPressListener
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private var backPressListener: BackPressListener? = null
    private lateinit var navController: NavController

    private val prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(prefsListener)

        navController =
            (supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment).navController

        with(mainViewModel) {
            backPressListener.observe(this@MainActivity, { listener ->
                this@MainActivity.backPressListener = listener
            })
        }

        bottom_nav.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { controller, destination, _ ->
            when (destination.id) {
                R.id.movieDetailsFragment -> {
//                    fragment.animateHideDown()
                    window.navigationBarColor =
                        ContextCompat.getColor(this, R.color.colorSurfaceDark)
                }
                else -> {
//                    fragment.animateShowUp()
                    window.navigationBarColor = ContextCompat.getColor(this, R.color.colorSurface)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (backPressListener == null || backPressListener?.onBackPressed() == true) {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(prefsListener)
    }
}
