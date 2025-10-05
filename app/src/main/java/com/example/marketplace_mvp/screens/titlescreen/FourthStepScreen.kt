package com.example.marketplace_mvp.screens.titlescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.example.marketplace_mvp.AppSettings
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.ui.theme.AppTheme
import com.google.android.material.button.MaterialButton

class FourthStepScreen : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    val navController = (activity as? AppCompatActivity)
                        ?.supportFragmentManager
                        ?.findFragmentById(R.id.containerView)
                        ?.findNavController()

                    navController?.let {
                        FourthStepOnboardingScreen(navController = it)
                    }
                }
            }
        }
    }
}

@Composable
fun FourthStepOnboardingScreen(navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                AppSettings(context).setFirstLaunchCompleted()

                navController.setGraph(R.navigation.navigation_graph)
            }
            .background(MaterialTheme.colorScheme.background)
    ) {
        Image(
            painter = painterResource(id = R.drawable.onboard_end),
            contentDescription = "fourth step image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
