package org.example.fakeshop_clients.features.search_bar.presentation.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class SearchBarDimensions(
    val searchBarHeightPx: Float,
    val searchBarHeightDp: Dp,
    val statusBarHeightPx: Float,
    val statusBarHeightDp: Dp,
    val totalHeightPx: Float,
    val totalHeightDp: Dp
)

@Composable
fun rememberSearchBarDimensions(): SearchBarDimensions {
    val density = LocalDensity.current
    val statusBarHeight = WindowInsets.statusBars.getTop(density)

    return remember(density, statusBarHeight) {
        val searchBarHeightDp = 72.dp
        val searchBarHeightPx = with(density) { searchBarHeightDp.toPx() }

        val statusBarHeightDp = with(density) { statusBarHeight.toDp() }
        val statusBarHeightPxFloat = statusBarHeight.toFloat()

        val totalHeightPx = searchBarHeightPx + statusBarHeightPxFloat
        val totalHeightDp = searchBarHeightDp + statusBarHeightDp

        SearchBarDimensions(
            searchBarHeightPx = searchBarHeightPx,
            searchBarHeightDp = searchBarHeightDp,
            statusBarHeightPx = statusBarHeightPxFloat,
            statusBarHeightDp = statusBarHeightDp,
            totalHeightPx = totalHeightPx,
            totalHeightDp = totalHeightDp
        )
    }
}