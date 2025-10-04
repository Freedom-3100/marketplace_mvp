package com.example.marketplace_mvp.screens.titlescreen

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.AppSettings
import com.example.marketplace_mvp.R

class SecondStepScreen: Fragment(R.layout.fragment_onboarding_second_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        // Обработчик клика по всей области (как кнопка)
        view.setOnClickListener {
            navigateToDefaultScreen()
        }

        // Меняем курсор на кликабельный
        view.isClickable = true
        view.foreground = ContextCompat.getDrawable(requireContext(), android.R.drawable.btn_default)
    }

    private fun navigateToDefaultScreen() {
        val appSettings = AppSettings(requireContext())
        appSettings.setFirstLaunchCompleted()
        findNavController().navigate(R.id.action_secondStepScreen2_to_thirdStepScreen2)
    }
}