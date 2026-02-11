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
import fakeshop_clients.composeapp.generated.resources.Res
import fakeshop_clients.composeapp.generated.resources.tab_favorites
import fakeshop_clients.composeapp.generated.resources.tab_home
import fakeshop_clients.composeapp.generated.resources.tab_notifications
import fakeshop_clients.composeapp.generated.resources.tab_profile
import org.jetbrains.compose.resources.StringResource

enum class BottomNavItem(
    val route: String,
    val titleRes: StringResource,
    val icon: ImageVector,
    val selectedIcon: ImageVector = icon
) {
    HOME(
        route = Route.Home.route,
        titleRes = Res.string.tab_home,
        icon = Icons.Outlined.Home,
        selectedIcon = Icons.Filled.Home
    ),
    FAVORITES(
        route = Route.Favorites.route,
        titleRes = Res.string.tab_favorites,
        icon = Icons.Outlined.FavoriteBorder,
        selectedIcon = Icons.Filled.Favorite
    ),
    NOTIFICATIONS(
        route = Route.Notifications.route,
        titleRes = Res.string.tab_notifications,
        icon = Icons.Outlined.Notifications,
        selectedIcon = Icons.Filled.Notifications
    ),
    PROFILE(
        route = Route.Profile.route,
        titleRes = Res.string.tab_profile,
        icon = Icons.Outlined.Person,
        selectedIcon = Icons.Filled.Person
    )
}
