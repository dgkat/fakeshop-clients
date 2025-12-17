package org.example.fakeshop_clients.features.search_bar.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity

class SearchBarScrollState(
    private val searchBarHeightPx: Float,
    private val onScrollOffsetChange: (Float) -> Unit
) {
    private var scrollOffset by mutableFloatStateOf(0f)

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            val delta = available.y
            val newOffset = (scrollOffset + delta).coerceIn(-searchBarHeightPx, 0f)

            scrollOffset = newOffset
            onScrollOffsetChange(scrollOffset)

            return Offset.Zero
        }

        override suspend fun onPreFling(available: Velocity): Velocity {
            // Snap to fully visible or fully hidden
            scrollOffset = if (scrollOffset < -(searchBarHeightPx / 2)) {
                -searchBarHeightPx
            } else {
                0f
            }
            onScrollOffsetChange(scrollOffset)
            return Velocity.Zero
        }
    }
}

@Composable
fun rememberSearchBarScrollState(
    onScrollOffsetChange: (Float) -> Unit
): SearchBarScrollState {
    val dimensions = rememberSearchBarDimensions()

    return remember(dimensions.totalHeightPx) {
        SearchBarScrollState(
            searchBarHeightPx = dimensions.totalHeightPx,
            onScrollOffsetChange = onScrollOffsetChange
        )
    }
}