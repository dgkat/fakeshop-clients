package org.example.fakeshop_clients.features.search_bar.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.features.search.domain.models.SearchResult
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior
import org.example.fakeshop_clients.features.search.presentation.SearchState

@Composable
fun SearchBarLayout(
    searchUiState: SearchState,
    scrollState: SearchBarScrollState,
    behavior: SearchBarBehavior,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onDismissSearch: () -> Unit,
    onResultClick: (result: SearchResult, position: Int) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit
) {

    val dimensions = rememberSearchBarDimensions()

    Box(modifier = modifier.fillMaxSize()) {
        content(
            PaddingValues(
                top = if (behavior == SearchBarBehavior.HIDDEN) {
                    0.dp
                } else {
                    dimensions.searchBarHeightDp
                }
            )
        )

        if (searchUiState.isActive && behavior != SearchBarBehavior.HIDDEN) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onDismissSearch()
                    }
            )
        }

        // Search bar overlay
        SearchBarOverlay(
            uiState = searchUiState,
            scrollState = scrollState,
            behavior = behavior,
            onQueryChange = onQueryChange,
            onClearQuery = onClearQuery,
            onDismissSearch = onDismissSearch,
            onResultClick = onResultClick,
            modifier = Modifier.statusBarsPadding()
        )
    }
}