package org.example.fakeshop_clients.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    HOME(
        route = Route.Home.route,
        title = "Home",
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    FAVORITES(
        route = Route.Favorites.route,
        title = "Favorites",
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite
    ),
    NOTIFICATIONS(
        route = Route.Notifications.route,
        title = "Notifications",
        icon = Icons.Outlined.Notifications,
        selectedIcon = Icons.Filled.Notifications
    ),
    PROFILE(
        route = Route.Profile.route,
        title = "Profile",
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}