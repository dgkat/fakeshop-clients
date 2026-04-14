package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore

class FavoritesViewModel(
    val favoritesStore: FavoritesViewStore,
    val recentsStore: RecentsViewStore,
    val profileService: ProfileService
) {
    val favoritesState = favoritesStore.state
    val recentsState = recentsStore.state

    fun onFavoritesEvent(event: FavoritesEvent) {
        favoritesStore.onEvent(event)
    }

    fun onRecentsEvent(event: org.example.fakeshop_clients.features.recents.presentation.RecentsEvent) {
        recentsStore.onEvent(event)
    }

    fun checkNotificationPermission() {
        favoritesStore.checkNotificationPermission()
    }
}
