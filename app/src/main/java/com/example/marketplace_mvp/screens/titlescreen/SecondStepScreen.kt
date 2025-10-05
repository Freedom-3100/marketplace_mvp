package com.example.marketplace_mvp.screens.titlescreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.AppSettings
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.ui.theme.AppTheme

class SecondStepScreen : Fragment() {

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
                        SecondStepOnboardingScreen(navController = it)
                    }
                }
            }
        }
    }
}

@Composable
fun SecondStepOnboardingScreen(navController: NavController) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                val appSettings = AppSettings(context)
                appSettings.setFirstLaunchCompleted()
                navController.navigate(R.id.action_secondStepScreen2_to_thirdStepScreen2)
            }
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "welcome image",
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(220.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Добро пожаловать в приложение RuStore!\nДавайте ознакомимся с интерфейсом.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}