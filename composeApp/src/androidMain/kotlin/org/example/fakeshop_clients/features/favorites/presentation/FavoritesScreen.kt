package org.example.fakeshop_clients.features.favorites.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.tab_favorites
import fakeshop_clients.composeapp.generated.resources.tab_recently_seen
import org.example.fakeshop_clients.features.favorites.presentation.components.ErrorContent
import org.example.fakeshop_clients.features.favorites.presentation.components.FavoritesEmptyContent
import org.example.fakeshop_clients.features.favorites.presentation.components.LoadingContent
import org.example.fakeshop_clients.features.favorites.presentation.components.LoginRequiredContent
import org.example.fakeshop_clients.features.favorites.presentation.components.NotificationPermissionBanner
import org.example.fakeshop_clients.features.favorites.presentation.components.ProductGrid
import org.example.fakeshop_clients.features.favorites.presentation.components.RecentsEmptyContent
import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    onProductClick: (String) -> Unit,
    onGoToProfile: () -> Unit,
    contentPadding: PaddingValues,
    viewModel: FavoritesViewModel = koinViewModel()
) {
    val favoritesState by viewModel.favoritesState.collectAsStateWithLifecycle()

    when {
        favoritesState.error is FavoritesError.NotLoggedIn -> LoginRequiredContent(
            onGoToProfile = onGoToProfile,
            modifier = Modifier.padding(contentPadding)
        )
        favoritesState.isLoading && favoritesState.products.isEmpty() && favoritesState.error == null ->
            LoadingContent(modifier = Modifier.padding(contentPadding))
        else -> FavoritesTabbedContent(
            viewModel = viewModel,
            onProductClick = onProductClick,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun FavoritesTabbedContent(
    viewModel: FavoritesViewModel,
    onProductClick: (String) -> Unit,
    contentPadding: PaddingValues
) {
    val tabs = listOf(
        stringResource(Res.string.tab_favorites),
        stringResource(Res.string.tab_recently_seen)
    )
    val pagerState = rememberPagerState { tabs.size }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = contentPadding.calculateTopPadding())
    ) {
        val favState by viewModel.favoritesState.collectAsStateWithLifecycle()
        NotificationPermissionBanner(
            permissionStatus = favState.notificationPermissionStatus,
            showBanner = favState.showNotificationBanner,
            onPermissionResult = { granted ->
                viewModel.onFavoritesEvent(FavoritesEvent.NotificationPermissionResult(granted))
            }
        )

        TabRow(
            selectedTabIndex = pagerState.currentPage,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                    text = { Text(title) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            when (page) {
                0 -> FavoritesTabContent(
                    viewModel = viewModel,
                    onProductClick = onProductClick
                )
                1 -> RecentsTabContent(
                    viewModel = viewModel,
                    onProductClick = onProductClick
                )
            }
        }
    }
}

@Composable
private fun FavoritesTabContent(
    viewModel: FavoritesViewModel,
    onProductClick: (String) -> Unit
) {
    val state by viewModel.favoritesState.collectAsStateWithLifecycle()

    state.error?.let {
        ErrorContent(
            onRetry = { viewModel.onFavoritesEvent(FavoritesEvent.Retry) }
        )
        return
    }

    if (state.isLoading) {
        LoadingContent()
        return
    }

    if (state.products.isEmpty()) {
        FavoritesEmptyContent()
        return
    }

    ProductGrid(
        products = state.products,
        onProductClick = onProductClick,
        onRemoveFavorite = { productId ->
            viewModel.onFavoritesEvent(FavoritesEvent.RemoveFavorite(productId))
        }
    )
}

@Composable
private fun RecentsTabContent(
    viewModel: FavoritesViewModel,
    onProductClick: (String) -> Unit
) {
    val state by viewModel.recentsState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onRecentsEvent(RecentsEvent.LoadRecents)
    }

    state.error?.let {
        ErrorContent(
            onRetry = { viewModel.onRecentsEvent(RecentsEvent.Retry) }
        )
        return
    }

    if (state.isLoading) {
        LoadingContent()
        return
    }

    if (state.products.isEmpty()) {
        RecentsEmptyContent()
        return
    }

    ProductGrid(
        products = state.products,
        onProductClick = onProductClick,
        onRemoveFavorite = null
    )
}
