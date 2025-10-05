package com.example.marketplace_mvp.screens.titlescreen

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.marketplace_mvp.AppSettings
import com.example.marketplace_mvp.R
import com.google.android.material.button.MaterialButton

class FourthStepScreen : Fragment(R.layout.fragment_onboarding_fourth_screen) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val startButton: MaterialButton = view.findViewById(R.id.startButton)

        startButton.setOnClickListener {
            // Сохраняем, что онбординг пройден
            AppSettings(requireContext()).setFirstLaunchCompleted()

            // Получаем NavController
            val navHostFragment = requireActivity()
                .supportFragmentManager
                .findFragmentById(R.id.containerView) as NavHostFragment
            val navController = navHostFragment.navController

            // Меняем граф на основной
            navController.setGraph(R.navigation.navigation_graph)
        }
    }
}