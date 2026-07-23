package org.example.fakeshop_clients.features.search_bar.presentation.components

import androidx.compose.animation.core.animate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Velocity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SearchBarScrollState(
    private val searchBarHeightPx: Float,
    private val scope: CoroutineScope,
) {
    private var scrollOffset by mutableFloatStateOf(0f)

    private var snapJob: Job? = null

    val offset: Float get() = scrollOffset

    fun reset() {
        snapJob?.cancel()
        scrollOffset = 0f
    }

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            snapJob?.cancel()
            scrollOffset = (scrollOffset + available.y).coerceIn(-searchBarHeightPx, 0f)
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            snapToNearest()
            return Velocity.Zero
        }
    }

    private fun snapToNearest() {
        val target = if (scrollOffset < -(searchBarHeightPx / 2)) -searchBarHeightPx else 0f
        snapJob?.cancel()
        if (target != scrollOffset) {
            snapJob = scope.launch {
                animate(initialValue = scrollOffset, targetValue = target) { value, _ ->
                    scrollOffset = value
                }
            }
        }
    }
}

@Composable
fun rememberSearchBarScrollState(): SearchBarScrollState {
    val dimensions = rememberSearchBarDimensions()
    val scope = rememberCoroutineScope()

    return remember(dimensions.totalHeightPx, scope) {
        SearchBarScrollState(searchBarHeightPx = dimensions.totalHeightPx, scope = scope)
    }
}
