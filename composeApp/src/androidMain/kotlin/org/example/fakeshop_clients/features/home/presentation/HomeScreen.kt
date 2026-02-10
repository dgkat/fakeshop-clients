package org.example.fakeshop_clients.features.home.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.fakeshop_clients.features.home.presentation.components.HomeContent
import org.example.fakeshop_clients.features.search_bar.presentation.components.rememberSearchBarScrollState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    productListViewModel: ProductListViewModel = koinViewModel(),
    contentPadding: PaddingValues,
    onScrollOffsetChange: (Float) -> Unit,
    onProductClick: (String) -> Unit,
) {
    val scrollState = rememberSearchBarScrollState(
        onScrollOffsetChange = onScrollOffsetChange
    )

    val uiState by productListViewModel.uiState.collectAsStateWithLifecycle()
    HomeContent(
        productListState = uiState,
        onProductClick = onProductClick,
        onRetry = {},
        modifier = Modifier,
        contentPadding = contentPadding,
        scrollState = scrollState
    )
}

@Composable
fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = stringResource(R.string.loading_products),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Text(
                text = "😔",
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = stringResource(R.string.error_generic),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}