package com.example.marketplace_mvp.screens.toolbar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.marketplace_mvp.ui.theme.AppTheme
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.R
import com.example.marketplace_mvp.firestore.AppsViewModel

class SearchFragment : Fragment(R.layout.search_fragment) {

    private val viewModel: AppsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val composeView = view as? ComposeView
        composeView?.setContent {
            AppTheme {
                SearchScreen(viewModel = viewModel)
            }
        }

        // Load initial data
        viewModel.loadApps()
    }
}

private fun AppsViewModel.loadApps() {
    TODO("Not yet implemented")
}

@Composable
fun SearchScreen(viewModel: AppsViewModel) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isSearching by viewModel.isSearching.collectAsState()
    val apps by viewModel.apps.collectAsState()

    // Sample categories (you might want to move this to ViewModel)
    val categories1 = remember {
        listOf(
            CategoryItem(
                name = "Экшен",
                icon = Icons.Filled.ScubaDiving,
                onClick = { /* Navigate to action games */ },
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            // ... your other categories ...
            CategoryItem(
                name = "Настольные",
                icon = Icons.Filled.TableRestaurant,
                onClick = { /* Navigate to board games */ },
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
            )
        )
    }

    val categories2 = remember {
        listOf(
            CategoryItem(
                name = "Развлечения",
                icon = Icons.Filled.Festival,
                onClick = { /* Navigate to entertainment */ },
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            // ... your other categories ...
            CategoryItem(
                name = "Фотография",
                icon = Icons.Filled.PhotoCamera,
                onClick = { /* Navigate to photography */ },
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
            )
        )
    }

    // Show search results or categories based on search state
    if (searchQuery.isNotEmpty()) {
        SearchResultsScreen(
            searchQuery = searchQuery,
            searchResults = searchResults,
            isSearching = isSearching,
            onSearchQueryChange = { viewModel.searchApps(it) },
            onClearSearch = { viewModel.clearSearch() }
        )
    } else {
        CategoryPage(
            categories1 = categories1,
            categories2 = categories2,
            title = "Каталог",
            onSearchQueryChange = { viewModel.searchApps(it) }
        )
    }
}

@Composable
fun SearchResultsScreen(
    searchQuery: String,
    searchResults: List<AppInfo>,
    isSearching: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            SearchBar(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                onClearSearch = onClearSearch,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Search Results Title
            Text(
                text = "Результаты поиска: \"$searchQuery\"",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when {
                isSearching -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(32.dp)
                    )
                }
                searchResults.isEmpty() -> {
                    Text(
                        text = "Ничего не найдено",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                else -> {
                    // Display search results
                    LazyColumn {
                        items(searchResults) { app ->
                            AppSearchResultItem(app = app)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppSearchResultItem(app: AppInfo) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { /* Handle app click - navigate to details */ },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App icon - you can use Coil or similar for image loading
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = app.name.take(2).uppercase(),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = app.name,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = app.category,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Rating
            Text(
                text = app.mark,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun CategoryPage(
    categories1: List<CategoryItem>,
    categories2: List<CategoryItem>,
    title: String,
    onSearchQueryChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 14.dp, start = 14.dp, end = 14.dp, top = 14.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            // Search Bar
            SearchBar(
                searchQuery = "",
                onSearchQueryChange = onSearchQueryChange,
                onClearSearch = { /* Clear handled by parent */ },
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Page title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category grid
            CategoryGridScreen(
                categories1 = categories1,
                categories2 = categories2,
                columns = 2
            )
        }
    }
}

@Composable
fun SearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf(searchQuery) }

    LaunchedEffect(searchQuery) {
        text = searchQuery
    }

    TextField(
        value = text,
        onValueChange = {
            text = it
            onSearchQueryChange(it)
        },
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        placeholder = {
            Text(
                text = "Поиск приложений...",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    onClearSearch()
                }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Clear search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        shape = RoundedCornerShape(12.dp),
        singleLine = true
    )
}

// Keep your existing CategoryGridScreen, CategoryButton, etc. unchanged
// 2. Horizontal button styled with theme
@Composable
fun CategoryButton(category: CategoryItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .clip(category.cornerShape)
            .background(MaterialTheme.colorScheme.surface)
            .clickable { category.onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = category.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = category.fontWeight // <-- apply font weight
        )
        Icon(
            imageVector = category.icon,
            contentDescription = category.name,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

// 3. Grid screen 2 columns × 6 rows
@Composable
fun CategoryGridScreen(
    categories1: List<CategoryItem>,
    categories2: List<CategoryItem>,
    columns: Int = 2
) {
    Column(
        modifier = Modifier.fillMaxWidth() // remove fillMaxSize
    ) {
        // First section
        Text(
            text = "Игры",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        categories1.chunked(columns).forEach { rowCategories ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowCategories.forEach { category ->
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryButton(category)
                    }
                }
                if (rowCategories.size < columns) {
                    repeat(columns - rowCategories.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }

        // Second section
        Text(
            text = "Открыть каталог",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 10.dp, top = 28.dp),
        )

        categories2.chunked(columns).forEach { rowCategories ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                rowCategories.forEach { category ->
                    Box(modifier = Modifier.weight(1f)) {
                        CategoryButton(category)
                    }
                }
                if (rowCategories.size < columns) {
                    repeat(columns - rowCategories.size) { Spacer(modifier = Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
fun CategoryPage(categories1: List<CategoryItem>, categories2: List<CategoryItem>, title: String) {
    // Outer container with background
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 14.dp, start = 14.dp, end = 14.dp, top = 14.dp) // Reduced top padding
    ) {
        // THIS COLUMN IS SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()) // <-- make entire content scrollable
        ) {
            // Search Bar at the top
            SearchBar(
                modifier = Modifier.padding(bottom = 24.dp),
                onSearch = { query ->
                    // Handle search functionality here
                    // You can filter categories based on the search query
                }
            )

            // Page title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Category grid
            CategoryGridScreen(
                categories1 = categories1,
                categories2 = categories2,
                columns = 2
            )
        }
    }
}

@Composable
fun PreviewCategoryPage() {
    // Wrap in your app theme so colors, typography, and shapes are applied
    AppTheme {
        // Sample categories for preview
        val categories1 = listOf(
            CategoryItem(
                name = "Экшен",
                icon = Icons.Filled.ScubaDiving,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Симуляторы",
                icon = Icons.Filled.Computer,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 15.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Головоломки",
                icon = Icons.Filled.Extension,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Приключения",
                icon = Icons.Filled.Landscape,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Гонки",
                icon = Icons.Filled.SportsScore,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Командные",
                icon = Icons.Filled.SportsBasketball,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Стратегии",
                icon = Icons.Filled.Map,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Спортивные",
                icon = Icons.Filled.Sports,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Ролевые",
                icon = Icons.Filled.Castle,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 15.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Настольные",
                icon = Icons.Filled.TableRestaurant,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
            )
        )

        val categories2 = listOf(
            CategoryItem(
                name = "Развлечения",
                icon = Icons.Filled.Festival,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 15.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Социальные",
                icon = Icons.Filled.PersonPin,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 15.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Работа",
                icon = Icons.Filled.Work,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Связь",
                icon = Icons.Filled.ConnectWithoutContact,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Музыка",
                icon = Icons.Filled.MusicNote,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 15.dp, bottomEnd = 5.dp)
            ),
            CategoryItem(
                name = "Фотография",
                icon = Icons.Filled.PhotoCamera,
                fontWeight = FontWeight.SemiBold,
                cornerShape = RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 5.dp, bottomEnd = 15.dp)
            )
        )

        // Call your styled page
        CategoryPage(
            categories1 = categories1,
            categories2 = categories2,
            title = "Каталог"
        )
    }
}






