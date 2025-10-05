package com.example.marketplace_mvp.screens.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.firestore.AppsViewModel
import com.example.marketplace_mvp.ui.components.InstallButton
import com.example.marketplace_mvp.ui.theme.AppTheme
import com.example.marketplace_mvp.ui.theme.PrimaryVariant
import com.example.marketplace_mvp.ui.theme.TextColor
import com.example.marketplace_mvp.ui.theme.TextSecondaryColor
import kotlinx.coroutines.launch
import kotlin.collections.forEach

class StripFragment : Fragment(R.layout.strip_fragment) {

    private val viewModel: AppsViewModel by viewModels()

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
                        StripWithAppsScreen(navController = it, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun StripWithAppsScreen(navController: NavController, viewModel: AppsViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadAllAppNames()
    }

    val appNames by viewModel.appNames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    val stripButtons = listOf(
        "Все", "Новинки", "Популярное", "Рекомендуем", "Акции", "Хиты"
    )
    var selectedStripIndex by remember { mutableStateOf(0) }

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val filteredApps = remember(selectedStripIndex, appNames) {

        appNames
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (isLoading && appNames.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }

        if (!message.isNullOrBlank()) {
            Text(
                text = message!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(16.dp)
            )
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(stripButtons) { index, text ->
                        CategoryButton(
                            text = text,
                            isSelected = index == selectedStripIndex,
                            onClick = {
                                selectedStripIndex = index
                                scope.launch { listState.animateScrollToItem(0) }
                            }
                        )
                    }
                }
            }

            if (filteredApps.isNotEmpty()) {
                items(filteredApps) { appName ->
                    AppCard(
                        appName = appName,
                        navController = navController,
                        viewModel = viewModel,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    )
                }
            } else if (!isLoading) {
                item {
                    Text(
                        text = "Приложения не найдены",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = TextSecondaryColor
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface,
            contentColor = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onPrimaryContainer
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

