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
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import androidx.fragment.app.viewModels
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.ui.components.InstallButton
import com.example.marketplace_mvp.ui.theme.AppTheme
import com.example.marketplace_mvp.ui.theme.TextColor
import coil.compose.AsyncImage
import com.example.marketplace_mvp.firestore.AppsViewModel

class ApplicationCard : Fragment() {

    private val viewModel: AppsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val appName = arguments?.getString("appName") ?: "Sample App"

        // Подгружаем данные приложения
        viewModel.loadAppInfoByName(appName)

        return ComposeView(requireContext()).apply {
            setContent {
                AppTheme {
                    ApplicationCardScreen(
                        activity = requireActivity(),
                        appName = appName,
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplicationCardScreen(
    activity: FragmentActivity,
    appName: String,
    viewModel: AppsViewModel
) {
    // Берем данные из ViewModel
    val appInfo by remember { derivedStateOf { viewModel.cachedApps[appName] } }

    var showFullscreen by remember { mutableStateOf(false) }
    var selectedScreenshot by remember { mutableStateOf(0) }

    // Если скриншоты не хранятся в модели, используем заглушки
    val screenshots = appInfo?.screenshots ?: listOf(
        "https://via.placeholder.com/400x800.png?text=Screenshot+1",
        "https://via.placeholder.com/400x800.png?text=Screenshot+2",
        "https://via.placeholder.com/400x800.png?text=Screenshot+3"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(appInfo?.name ?: appName) },
                navigationIcon = {
                    IconButton(onClick = { activity.onBackPressedDispatcher.onBackPressed() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(innerPadding)
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
                        if (!appInfo?.imageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = appInfo!!.imageUrl,
                                contentDescription = "${appInfo!!.name} icon",
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text("📱", fontSize = 32.sp)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = appInfo?.name ?: appName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = appInfo?.creator ?: "Unknown Publisher",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        // Здесь можно добавить info о покупках, если нужно
                    }
                }

                // --- Info Cards ---
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    item { InfoCard { RatingCard(appInfo?.mark ?: "0", 0) } } // reviewsCount можно добавить, если есть
                    item { DividerCard() }
                    item { InfoCard { TextInfo("Age", appInfo?.ageRating ?: "12+") } }
                    item { DividerCard() }
                    item { InfoCard { TextInfo("Size", appInfo?.size ?: "1.2 GB") } }
                    item { DividerCard() }
                    item { InfoCard { TextInfo("Downloads", appInfo?.downloads ?: "1M+") } }
                }

                // --- Install Button ---
                if (!appInfo?.cleanApkUrl.isNullOrBlank()) {
                    InstallButton(apkUrl = appInfo!!.cleanApkUrl)
                }

                // --- Description ---
                Text("Описание", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = appInfo?.description
                        ?: "This is a detailed description of the application. You can show features, screenshots, and other relevant info here.",
                    style = MaterialTheme.typography.bodySmall
                )

                ScreenshotSection(screenshots) { index ->
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

// --- Info Cards and Screenshots (оставляем как раньше) ---
@Composable
fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.width(100.dp).height(80.dp),
        shape = RoundedCornerShape(12.dp)
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
fun RatingCard(mark: String, reviewsCount: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Star, contentDescription = "star icon", modifier = Modifier.size(16.dp))
            Text(mark, fontSize = 16.sp, color = TextColor)
        }
        Text("$reviewsCount reviews", style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun TextInfo(title: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(title, style = MaterialTheme.typography.bodySmall)
        Text(value, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun DividerCard() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(80.dp)
            .background(Color.Gray.copy(alpha = 0.2f))
    )
}

@Composable
fun ScreenshotSection(screenshots: List<String>, onScreenshotClick: (Int) -> Unit) {
    Column {
        Text("Скриншоты", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
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
                        placeholder = ColorPainter(MaterialTheme.colorScheme.surfaceVariant),
                        error = ColorPainter(MaterialTheme.colorScheme.error)
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
    BackHandler(enabled = true) { onDismiss() }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = screenshots[currentIndex],
                contentDescription = "Screenshot ${currentIndex + 1}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            if (currentIndex > 0) {
                IconButton(
                    onClick = { currentIndex-- },
                    modifier = Modifier.align(Alignment.CenterStart).padding(16.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Previous", tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
            if (currentIndex < screenshots.lastIndex) {
                IconButton(
                    onClick = { currentIndex++ },
                    modifier = Modifier.align(Alignment.CenterEnd).padding(16.dp)
                ) {
                    Icon(Icons.Filled.ArrowForward, contentDescription = "Next", tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}