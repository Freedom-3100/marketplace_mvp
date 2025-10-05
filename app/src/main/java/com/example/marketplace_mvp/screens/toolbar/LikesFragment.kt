package com.example.marketplace_mvp.screens.toolbar

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import coil.compose.AsyncImage
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.firestore.AppsViewModel
import com.example.marketplace_mvp.ui.components.InstallButton
import com.example.marketplace_mvp.ui.theme.*
import kotlinx.coroutines.launch

class LikesFragment : Fragment(R.layout.likes_fragment) {

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
                        LikesScreen(navController = it, viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun LikesScreen(navController: NavController, viewModel: AppsViewModel) {
    LaunchedEffect(Unit) {
        viewModel.loadAllAppNames()
    }

    val appNames by viewModel.appNames.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (isLoading && appNames.isEmpty()) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(16.dp)
            )
        }

        if (!message.isNullOrBlank()) {
            Text(message!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
        }

        AppCategories(
            navController = navController,
            appNames = appNames,
            viewModel = viewModel
        )
    }
}

@Composable
fun AppCategories(
    navController: NavController,
    appNames: List<String>,
    viewModel: AppsViewModel
) {
    val categories = listOf("Популярное", "Для вас", "Что нового")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        items(categories) { category ->
            CategorySection(
                categoryName = category,
                appNames = appNames,
                navController = navController,
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CategorySection(
    categoryName: String,
    appNames: List<String>,
    navController: NavController,
    viewModel: AppsViewModel
) {
    if (appNames.isEmpty()) return

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val appGroups = appNames.chunked(3)

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = categoryName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            IconButton(onClick = {
                val next = (listState.firstVisibleItemIndex + 1).coerceAtMost(appGroups.lastIndex)
                scope.launch { listState.animateScrollToItem(next) }
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Прокрутить",
                    tint = TextSecondaryColor
                )
            }
        }

        LazyRow(
            state = listState,
            horizontalArrangement = Arrangement.spacedBy(0.dp),
            contentPadding = PaddingValues(horizontal = 14.dp)
        ) {
            items(appGroups) { group ->
                Column(
                    modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp - 32.dp)
                ) {
                    group.forEach { appName ->
                        AppCard(
                            appName = appName,
                            navController = navController,
                            viewModel = viewModel,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(
    appName: String,
    navController: NavController,
    viewModel: AppsViewModel,
    modifier: Modifier = Modifier
) {
    val appInfo = viewModel.cachedApps[appName]

    LaunchedEffect(appName) {
        viewModel.loadAppInfoByName(appName)
    }

    Card(
        modifier = modifier.clickable {
            val bundle = Bundle().apply { putString("appName", appName) }
            navController.navigate(R.id.applicationCard, bundle)
        },
        colors = CardDefaults.cardColors(containerColor = BackgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            if (!appInfo?.imageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = appInfo.imageUrl,
                    contentDescription = "${appInfo.name} icon",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp)) // 👈 this actually rounds the image itself
                        .background(PrimaryVariant),       // optional background color
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))   // 👈 also clip placeholder
                        .background(PrimaryVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (appInfo == null) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text(text = "📱", fontSize = 24.sp)
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .weight(1f)
            ) {
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextColor),
                    fontSize = 16.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                if (appInfo != null) {
                    Text(
                        text = appInfo.description,
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryColor),
                        fontSize = 12.sp,
                        maxLines = 1
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Рейтинг",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = appInfo.mark,
                            style = MaterialTheme.typography.bodyLarge.copy(color = TextColor),
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            if (!appInfo?.cleanApkUrl.isNullOrBlank()) {
                AppTheme { InstallButton(apkUrl = appInfo.cleanApkUrl) }
            } else {
                Box(
                    modifier = Modifier.size(80.dp, 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (appInfo == null) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
            }
        }
    }
}
