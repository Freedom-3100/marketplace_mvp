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
import androidx.core.content.edit
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.R



class MainActivity : AppCompatActivity() {

    private lateinit var appSettings: AppSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appSettings = AppSettings(this)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.containerView) as NavHostFragment
        val navController = navHostFragment.navController

        val graphId = if (appSettings.shouldShowFirstLaunch()) {
            R.navigation.nav_graph_onboarding
        } else {
            R.navigation.navigation_graph
        }

        navController.setGraph(graphId, null)
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




