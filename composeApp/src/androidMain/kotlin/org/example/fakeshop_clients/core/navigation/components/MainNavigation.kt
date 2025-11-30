package org.example.fakeshop_clients.core.navigation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import org.example.fakeshop_clients.core.navigation.BottomNavItem
import org.example.fakeshop_clients.core.navigation.Route
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesScreen
import org.example.fakeshop_clients.features.home.presentation.HomeScreen
import org.example.fakeshop_clients.features.notifications.presentation.NotificationsScreen
import org.example.fakeshop_clients.features.product_detail.presentation.ProductDetailScreen
import org.example.fakeshop_clients.features.profile.presentation.ProfileScreen

@Composable
fun MainNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val activeTab = remember(currentRoute) {
        when {
            currentRoute in BottomNavItem.entries.map { it.route } -> currentRoute
            else -> null
        }
    }

    Scaffold(
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
                    }
                )
            }

            composable(Route.Favorites.route) {
                FavoritesScreen()
            }

            composable(Route.Notifications.route) {
                NotificationsScreen()
            }

            composable(Route.Profile.route) {
                ProfileScreen()
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
                    onNavigateBack = { navController.navigateUp() }
                )
            }
        }
    }
}