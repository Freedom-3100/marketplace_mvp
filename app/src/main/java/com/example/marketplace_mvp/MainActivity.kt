package com.example.marketplace_mvp

import android.content.Context
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.fragment.NavHostFragment
import com.example.marketplace_mvp.databinding.ActivityMainBinding
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

class MainActivity : AppCompatActivity() {
    private lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appSettings = AppSettings(this)

        // Ждем когда NavController будет готов
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.containerView) as NavHostFragment
        navHostFragment.let { fragment ->
            fragment.findNavController().let { navController ->
                navigateToStartScreen(navController)
            }
        }
    }

    private fun navigateToStartScreen(navController: NavController) {
        val startDestination = if (appSettings.shouldShowFirstLaunch()) {
            R.id.titleScreen
        } else {
            R.id.toolBarFragment
        }

        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setPopUpTo(R.id.navigation_graph, true) // Очищает back stack
            .build()

        navController.navigate(startDestination, null, navOptions)
    }
}

class AppSettings(private val context: Context) {
    private val sharedPref = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)

    fun shouldShowFirstLaunch(): Boolean {
        return sharedPref.getBoolean("first_launch", true)
    }

    fun setFirstLaunchCompleted() {
        sharedPref.edit { putBoolean("first_launch", false) }
    }
}