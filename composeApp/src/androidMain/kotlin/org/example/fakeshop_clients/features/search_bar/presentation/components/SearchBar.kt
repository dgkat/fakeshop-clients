package org.example.fakeshop_clients.features.search_bar.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.clear_search
import fakeshop_clients.composeapp.generated.resources.no_results
import fakeshop_clients.composeapp.generated.resources.search
import fakeshop_clients.composeapp.generated.resources.search_placeholder
import org.example.fakeshop_clients.features.search.domain.models.SearchResult
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior
import org.example.fakeshop_clients.features.search.presentation.SearchState
import org.jetbrains.compose.resources.stringResource

@Composable
fun SearchBarOverlay(
    uiState: SearchState,
    scrollOffset: Float,
    behavior: SearchBarBehavior,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onDismissSearch: () -> Unit,
    onResultClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    val dimensions = rememberSearchBarDimensions()

    // Calculate offset based on behavior
    val targetOffset = when (behavior) {
        SearchBarBehavior.HIDDEN -> -dimensions.totalHeightPx
        SearchBarBehavior.SCROLL_REACTIVE -> scrollOffset
        SearchBarBehavior.STATIC -> 0f
    }

    val animatedOffset by animateFloatAsState(
        targetValue = targetOffset,
        label = "searchBarOffset"
    )

    val showShadow by remember {
        derivedStateOf { scrollOffset < -5f || behavior == SearchBarBehavior.STATIC }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .graphicsLayer {
                translationY = animatedOffset
            }
    ) {
        // Search Bar with background
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensions.searchBarHeightDp)
                .shadow(
                    elevation = if (showShadow) 4.dp else 0.dp,
                    shape = RoundedCornerShape(0.dp)
                ),
            color = MaterialTheme.colorScheme.surfaceContainer,
            tonalElevation = if (showShadow) 2.dp else 0.dp
        ) {
            SearchBarContent(
                query = uiState.query,
                onQueryChange = onQueryChange,
                onClearQuery = onClearQuery
            )
        }

        // Search Results Dropdown
        AnimatedVisibility(
            visible = uiState.isActive && behavior != SearchBarBehavior.HIDDEN,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            SearchResultsDropdown(
                results = uiState.results,
                isLoading = uiState.isLoading,
                onResultClick = {
                    onResultClick(it)
                    onDismissSearch()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBarContent(
    query: String,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            placeholder = { Text(stringResource(Res.string.search_placeholder)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(Res.string.search)
                )
            },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = onClearQuery) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(Res.string.clear_search)
                        )
                    }
                }
            },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
            ),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun SearchResultsDropdown(
    results: List<SearchResult>,
    isLoading: Boolean,
    onResultClick: (SearchResult) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 8.dp
    ) {
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            results.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.no_results),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            else -> {
                LazyColumn {
                    items(results) { result ->
                        SearchResultItem(
                            result = result,
                            onClick = { onResultClick(result) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultItem(
    result: SearchResult,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: Add image loading with Coil if you have imageUrl

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp)
        ) {
            Text(
                text = result.productName,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
