package org.example.fakeshop_clients.features.home.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.error
import org.example.fakeshop_clients.core.presentation.components.ProductCard
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.home.presentation.ErrorState
import org.example.fakeshop_clients.features.home.presentation.LoadingState
import org.example.fakeshop_clients.features.home.presentation.productList.UiCategoryRow
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListState
import org.example.fakeshop_clients.features.search_bar.presentation.components.SearchBarScrollState
import org.jetbrains.compose.resources.stringResource

@Composable
fun HomeContent(
    productListState: ProductListState,
    onProductClick: (productId: String, position: Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    scrollState: SearchBarScrollState
) {

    val categories = productListState.categories

    if (categories.isNotEmpty()) {
        ProductListContent(
            categories = productListState.categories,
            favoritedProductIds = productListState.favoritedProductIds,
            onProductClick = onProductClick,
            onToggleFavorite = onToggleFavorite,
            modifier = modifier,
            contentPadding = contentPadding,
            scrollState = scrollState
        )
    }

    if (productListState.isLoading) {
        LoadingState(modifier = modifier)
    }

    productListState.error?.let {
        //TODO handle error
        ErrorState(
            message = stringResource(Res.string.error),
            onRetry = onRetry,
            modifier = modifier
        )
    }
}

@Composable
private fun ProductListContent(
    categories: List<UiCategoryRow>,
    favoritedProductIds: Set<String>,
    onProductClick: (productId: String, position: Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    scrollState: SearchBarScrollState
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollState.nestedScrollConnection),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding() + 16.dp,
            bottom = 16.dp,
        )
    ) {
        items(
            items = categories,
            key = { it.category }
        ) { categoryRow ->
            CategorySection(
                categoryRow = categoryRow,
                favoritedProductIds = favoritedProductIds,
                onProductClick = onProductClick,
                onToggleFavorite = onToggleFavorite,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategorySection(
    categoryRow: UiCategoryRow,
    favoritedProductIds: Set<String>,
    onProductClick: (productId: String, position: Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(vertical = 8.dp)
    ) {
        Text(
            text = categoryRow.category,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        ProductRow(
            products = categoryRow.products,
            favoritedProductIds = favoritedProductIds,
            onProductClick = onProductClick,
            onToggleFavorite = onToggleFavorite
        )
    }
}

@Composable
private fun ProductRow(
    products: List<UiBriefProduct>,
    favoritedProductIds: Set<String>,
    onProductClick: (productId: String, position: Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    val showStartShadow by remember { derivedStateOf { listState.canScrollBackward } }
    val showEndShadow by remember { derivedStateOf { listState.canScrollForward } }
    val startAlpha by animateFloatAsState(targetValue = if (showStartShadow) 1f else 0f)
    val endAlpha by animateFloatAsState(targetValue = if (showEndShadow) 1f else 0f)
    val surfaceColor = MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier.drawWithContent {
            drawContent()
            val shadowWidth = 48.dp.toPx()
            if (startAlpha > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(surfaceColor.copy(alpha = startAlpha), Color.Transparent),
                        startX = 0f,
                        endX = shadowWidth
                    )
                )
            }
            if (endAlpha > 0f) {
                drawRect(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color.Transparent, surfaceColor.copy(alpha = endAlpha)),
                        startX = size.width - shadowWidth,
                        endX = size.width
                    )
                )
            }
        }
    ) {
        LazyRow(
            state = listState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(
                items = products,
                key = { _, product -> product.id }
            ) { index, product ->
                ProductCard(
                    product = product,
                    // 0-based rank within the shelf, so CTR can be corrected for position bias
                    // later. It is not reconstructable after the fact.
                    onClick = { onProductClick(product.id, index) },
                    isFavorited = product.id in favoritedProductIds,
                    onToggleFavorite = { onToggleFavorite(product.id) }
                )
            }
        }
    }
}
