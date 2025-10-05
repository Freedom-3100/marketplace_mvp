package com.example.marketplace_mvp.screens.feedscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.ui.components.InstallButton
import com.example.marketplace_mvp.ui.theme.AppTheme
import com.example.marketplace_mvp.ui.theme.TextColor
import coil.compose.AsyncImage

class ApplicationCard: Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    ApplicationCardScreen(requireActivity())
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationCardScreen(
    activity: FragmentActivity,
    appName: String = "Sample App",
    publisher: String = "Sample Publisher",
    inAppPurchases: Boolean = true,
    rating: Float = 3.9f,
    reviewsCount: Int = 1200,
    ageRating: String = "12+",
    sizeInGb: String = "1.2 GB",
    totalDownloads: String = "1M+",
) {

    val screenshots = listOf(
        "https://via.placeholder.com/400x800.png?text=Screenshot+1",
        "https://via.placeholder.com/400x800.png?text=Screenshot+2",
        "https://via.placeholder.com/400x800.png?text=Screenshot+3"
    )

    var showFullscreen by remember { mutableStateOf(false) }
    var selectedScreenshot by remember { mutableStateOf(0) }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appName) },
                navigationIcon = {
                    IconButton(onClick = { activity.onBackPressedDispatcher.onBackPressed() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- Header Section ---
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("📱", fontSize = 32.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = appName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = publisher,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        if (inAppPurchases) {
                            Text(
                                text = "Contains in-app purchases",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSecondary,
                            )
                        }
                    }
                }

                // --- Horizontally scrollable info section ---
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Rating + reviews
                    item {
                        InfoCard {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(verticalAlignment = Alignment.CenterVertically,) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = "star icon",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(16.dp),
                                    )
                                    Text(
                                        text = "$rating",
                                        style = MaterialTheme.typography.bodyLarge.copy(color = TextColor),
                                        fontSize = 16.sp,
                                        maxLines = 1
                                    )
                                }
                                Text(
                                    text = "$reviewsCount reviews",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondary,
                                )
                            }
                        }
                    }

                    // Divider
                    item {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        )
                    }

                    // Age rating
                    item {
                        InfoCard {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Age",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSecondary,)
                                Text(ageRating, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        )
                    }

                    // Size
                    item {
                        InfoCard {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Size", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                Text(sizeInGb, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }

                    item {
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(80.dp)
                                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f))
                        )
                    }

                    // Total Downloads
                    item {
                        InfoCard {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Downloads", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                Text(totalDownloads, style = MaterialTheme.typography.bodyLarge)
                            }
                        }
                    }
                }

                // --- Install Button ---
                InstallButton(apkUrl = "https://firebasestorage.googleapis.com/...your_apk_link...")

                // --- App Description ---
                Text(
                    text = "Описание",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "This is a detailed description of the application. You can show features, screenshots, and other relevant info here.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground
                )

                ScreenshotSection(screenshots = screenshots) { index ->
                    selectedScreenshot = index
                    showFullscreen = true
                }
            }

            if (showFullscreen) {
                FullscreenScreenshotViewer(
                    activity = activity,
                    screenshots = screenshots,
                    initialIndex = selectedScreenshot,
                    onDismiss = { showFullscreen = false }
                )
            }
        }
    )
}

// --- InfoCard Composable ---
@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(80.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            content = content
        )
    }
}

@Composable
fun ScreenshotSection(
    screenshots: List<String>, // URLs or local resources
    onScreenshotClick: (Int) -> Unit
) {
    Column {
        Text(
            text = "Скриншоты",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(screenshots) { index, screenshotUrl ->
                Box(
                    modifier = Modifier
                        .width(200.dp)
                        .height(350.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onScreenshotClick(index) }
                ) {
                    AsyncImage(
                        model = screenshotUrl,
                        contentDescription = "Screenshot ${index + 1}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant), // light gray background
                        error = ColorPainter(MaterialTheme.colorScheme.error) // red if failed
                    )
                }
            }
        }
    }
}

@Composable
fun FullscreenScreenshotViewer(
    activity: FragmentActivity,
    screenshots: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(initialIndex) }

    // Handle system back button
    BackHandler(enabled = true) {
        onDismiss()
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false // fullscreen
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            // Fullscreen image
            AsyncImage(
                model = screenshots[currentIndex],
                contentDescription = "Screenshot ${currentIndex + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )

            // Left arrow
            if (currentIndex > 0) {
                IconButton(
                    onClick = { currentIndex-- },
                    modifier = Modifier.align(Alignment.CenterStart).padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Right arrow
            if (currentIndex < screenshots.lastIndex) {
                IconButton(
                    onClick = { currentIndex++ },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        }
    }
}


