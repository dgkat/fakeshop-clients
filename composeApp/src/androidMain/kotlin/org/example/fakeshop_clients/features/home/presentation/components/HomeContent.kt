package org.example.fakeshop_clients.features.home.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.error
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
    onProductClick: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues,
    scrollState: SearchBarScrollState
) {

    val categories = productListState.categories

    if (categories.isNotEmpty()) {
        ProductListContent(
            categories = productListState.categories,
            onProductClick = onProductClick,
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
    onProductClick: (String) -> Unit,
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
            start = 16.dp,
            end = 16.dp
        )
    ) {
        items(
            items = categories,
            key = { it.category }
        ) { categoryRow ->
            CategorySection(
                categoryRow = categoryRow,
                onProductClick = onProductClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun CategorySection(
    categoryRow: UiCategoryRow,
    onProductClick: (String) -> Unit,
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
            onProductClick = onProductClick
        )
    }
}

@Composable
private fun ProductRow(
    products: List<UiBriefProduct>,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(
                items = products,
                key = { it.id }
            ) { product ->
                ProductCard(
                    product = product,
                    onClick = { onProductClick(product.id) }
                )
            }
        }
    }
}

@Composable
private fun ProductCard(
    product: UiBriefProduct,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
            )

            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
