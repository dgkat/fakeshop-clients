package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus

data class FavoritesState(
    val products: List<UiBriefProduct> = emptyList(),
    val isLoading: Boolean = true,
    val error: FavoritesError? = null,
    val notificationPermissionStatus: NotificationPermissionStatus = NotificationPermissionStatus.NOT_DETERMINED,
    val showNotificationBanner: Boolean = false
)
