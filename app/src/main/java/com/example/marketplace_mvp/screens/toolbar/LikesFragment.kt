package com.example.marketplace_mvp.screens.toolbar

import android.R.attr.category
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import androidx.compose.foundation.layout.paddingFrom
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.provider.FontsContractCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.MainActivity
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.ui.theme.AppTheme
import com.example.marketplace_mvp.ui.theme.BackgroundColor
import com.example.marketplace_mvp.ui.theme.PrimaryVariant
import com.example.marketplace_mvp.ui.theme.SurfaceColor
import com.example.marketplace_mvp.ui.theme.TextColor
import com.example.marketplace_mvp.ui.theme.TextSecondaryColor
import kotlinx.coroutines.launch
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.*

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.marketplace_mvp.ui.components.InstallButton

data class Category(
    val name: String,
    val icon: ImageVector
)

class LikesFragment : Fragment(R.layout.likes_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // Оборачиваем весь контент в AppTheme
                AppTheme {
                    val navController = (activity as? AppCompatActivity)
                        ?.supportFragmentManager
                        ?.findFragmentById(R.id.containerView)
                        ?.findNavController()

                    navController?.let {
                        LikesScreen(it)
                    }
                }
            }
        }
    }
}

@Composable
fun LikesScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor) // фон из темы
    ) {
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "Интересное",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 0.dp),
            )
        }

        AppCategories(navController)
    }
}

@Composable
fun AppCategories(navController: NavController) {
    val categories = listOf("Популярное", "Для вас", "Что нового")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 12.dp)
    ) {
        items(categories) { category ->
            CategorySection(categoryName = category, navController = navController)
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun CategorySection(categoryName: String, navController: NavController) {
    val apps = List(15) { "$categoryName App $it" }
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Группируем приложения по 3 для вертикальной стопки
    val appGroups = apps.chunked(3)

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Заголовок + стрелочка
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
            IconButton(
                onClick = {
                    val visibleIndex = listState.firstVisibleItemIndex
                    val nextIndex = (visibleIndex + 1).coerceAtMost(appGroups.lastIndex)
                    scope.launch {
                        listState.animateScrollToItem(nextIndex)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                    contentDescription = "Scroll",
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
                    modifier = Modifier.width(LocalConfiguration.current.screenWidthDp.dp - 32.dp) // одна колонка на весь экран
                ) {
                    group.forEach { appName ->
                        AppCard(
                            appName = appName,
                            navController = navController,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppCard(appName: String, navController: NavController, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .clickable {
                navController.navigate(R.id.applicationCard)
            },
        colors = CardDefaults.cardColors(containerColor = BackgroundColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(PrimaryVariant, shape = RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📱", fontSize = 24.sp)
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
                Text(
                    text = appName,
                    style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondaryColor),
                    fontSize = 12.sp,
                    maxLines = 1,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "star icon",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(16.dp),
                    )
                    Text(
                        text = "3.9",
                        style = MaterialTheme.typography.bodyLarge.copy(color = TextColor),
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }
            }

            AppTheme {
                InstallButton(
                    apkUrl = "https://firebasestorage.googleapis.com/...your_apk_link..."
                )
            }
            //get button here
        }
    }
}