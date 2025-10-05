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
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.marketplace_mvp.R

class SearchFragment : Fragment(R.layout.search_fragment) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                PreviewCategoryPage()
            }
        }
    }
}

data class CategoryItem(
    val name: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val onClick: () -> Unit = {},
    val cornerShape: RoundedCornerShape = RoundedCornerShape(5.dp),
    val fontWeight: FontWeight = FontWeight.SemiBold
)

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

@Composable
fun CategoryGridScreen(
    categories1: List<CategoryItem>,
    categories2: List<CategoryItem>,
    columns: Int = 2
) {
    Column(
        modifier = Modifier.fillMaxWidth() // remove fillMaxSize
    ) {
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(bottom = 14.dp, start = 14.dp,end = 14.dp, top = 28.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(24.dp))

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

    AppTheme {
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

        CategoryPage(
            categories1 = categories1,
            categories2 = categories2,
            title = "Каталог"
        )
    }
}






