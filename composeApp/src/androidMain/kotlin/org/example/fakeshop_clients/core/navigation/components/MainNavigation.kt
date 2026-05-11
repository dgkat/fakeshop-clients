package org.example.fakeshop_clients.core.navigation.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.fakeshop_clients.core.navigation.BottomNavItem
import org.example.fakeshop_clients.core.navigation.Route
import org.example.fakeshop_clients.core.navigation.utils.getSearchBarBehavior
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesScreen
import org.example.fakeshop_clients.features.home.presentation.HomeScreen
import org.example.fakeshop_clients.features.notifications.presentation.NotificationsScreen
import org.example.fakeshop_clients.features.product_detail.presentation.ProductDetailScreen
import org.example.fakeshop_clients.features.profile.presentation.ProfileScreen
import org.example.fakeshop_clients.features.profile.presentation.components.LanguagePickerSection
import org.example.fakeshop_clients.features.notifications.presentation.NotificationEventBus
import org.example.fakeshop_clients.features.notifications.presentation.PushNotificationEvent
import org.example.fakeshop_clients.features.search.presentation.SearchBarBehavior
import org.example.fakeshop_clients.features.search.presentation.SearchEvent
import org.example.fakeshop_clients.features.search_bar.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search_bar.presentation.components.SearchBarLayout
import org.example.fakeshop_clients.features.search_bar.presentation.components.rememberSearchBarDimensions
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun MainNavigation(initialProductId: String? = null) {
    val navController = rememberNavController()

    LaunchedEffect(initialProductId) {
        initialProductId?.let { productId ->
            navController.navigate(Route.ProductDetail.createRoute(productId))
        }
    }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val searchViewModel: SearchViewModel = koinViewModel()
    val searchUiState by searchViewModel.state.collectAsStateWithLifecycle()

    var scrollOffset by remember { mutableFloatStateOf(0f) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        NotificationEventBus.events.collect { event ->
            when (event) {
                is PushNotificationEvent.PriceDrop -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.body,
                        actionLabel = "View",
                        duration = SnackbarDuration.Long
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        navController.navigate(Route.ProductDetail.createRoute(event.productId))
                    }
                }

                is PushNotificationEvent.OpenProduct -> {
                    navController.navigate(Route.ProductDetail.createRoute(event.productId))
                }
            }
        }
    }

    val activeTab = remember(currentRoute) {
        when {
            currentRoute in BottomNavItem.entries.map { it.route } -> currentRoute
            else -> null
        }
    }

    val searchBarBehavior = remember(currentRoute) {
        getSearchBarBehavior(currentRoute)
    }

    // Clear search when navigating to certain screens if needed
    LaunchedEffect(currentRoute, searchBarBehavior) {
        // Example: Clear search when going to payment screen
        // if (currentRoute == Route.Payment.route) {
        //     searchViewModel.clearQuery()
        // }
        if (searchBarBehavior != SearchBarBehavior.HIDDEN) {
            scrollOffset = 0f
        }
    }

    SearchBarLayout(
        searchUiState = searchUiState,
        scrollOffset = scrollOffset,
        behavior = searchBarBehavior,
        onQueryChange = { searchViewModel.onEvent(SearchEvent.QueryChanged(query = it)) },
        onClearQuery = { searchViewModel.onEvent(SearchEvent.ClearQuery) },
        onDismissSearch = { searchViewModel.onEvent(SearchEvent.CancelClicked) },
        onResultClick = { result ->
            navController.navigate(Route.ProductDetail.createRoute(result.productId))
        }
    ) {
        val searchBarDimensions = rememberSearchBarDimensions()
        val searchBarPadding = PaddingValues(top = searchBarDimensions.searchBarHeightDp)

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                BottomNavigationBar(
                    activeTab = activeTab,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(route) { inclusive = false }
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Route.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Route.Home.route) {
                    HomeScreen(
                        onProductClick = { productId ->
                            navController.navigate(Route.ProductDetail.createRoute(productId))
                        },
                        contentPadding = searchBarPadding,
                        onScrollOffsetChange = { scrollOffset = it }
                    )
                }

                composable(Route.Favorites.route) {
                    FavoritesScreen(
                        onProductClick = { productId ->
                            navController.navigate(Route.ProductDetail.createRoute(productId))
                        },
                        onGoToProfile = {
                            navController.navigate(Route.Profile.route) {
                                popUpTo(Route.Profile.route) { inclusive = false }
                                launchSingleTop = true
                            }
                        },
                        contentPadding = searchBarPadding
                    )
                }

                composable(Route.Notifications.route) {
                    NotificationsScreen(contentPadding = PaddingValues())
                }

                composable(Route.Profile.route) {
                    ProfileScreen(
                        languageSection = { LanguagePickerSection() },
                        contentPadding = searchBarPadding
                    )
                }

                composable(
                    route = Route.ProductDetail.route,
                    arguments = listOf(
                        navArgument("productId") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val productId =
                        backStackEntry.arguments?.getString("productId") ?: return@composable
                    ProductDetailScreen(
                        productId = productId,
                        contentPadding = searchBarPadding,
                        onScrollOffsetChange = { scrollOffset = it }
                    )
                }
            }
        }
    }
}