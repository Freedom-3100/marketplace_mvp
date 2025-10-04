package com.example.marketplace_mvp.screens.toolbar

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.marketplace_mvp.R
import com.google.android.material.button.MaterialButton
class StripFragment : Fragment(R.layout.strip_fragment) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategoryStrip(view)
    }

    private fun setupCategoryStrip(view: View) {
        val buttons = listOf("Все", "Новинки", "Популярное", "Рекомендуем", "Акции", "Хиты",
            "Все", "Новинки", "Популярное", "Рекомендуем", "Акции", "Хиты")

        val container = view.findViewById<LinearLayout>(R.id.buttonsContainer)

        buttons.forEach { buttonText ->
            val button = MaterialButton(requireContext()).apply {
                text = buttonText
                setOnClickListener {
                    Toast.makeText(context, "Нажата: $buttonText", Toast.LENGTH_SHORT).show()
                }
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    setMargins(0, 0, 16, 0)
                }
            }
            container.addView(button)
        }
    }
}
