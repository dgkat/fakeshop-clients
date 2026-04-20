package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore

class FavoritesViewModel(
    private val favoritesStore: FavoritesViewStore,
    private val recentsStore: RecentsViewStore
) {
    val favoritesState = favoritesStore.state
    val recentsState = recentsStore.state

    fun onFavoritesEvent(event: FavoritesEvent) {
        favoritesStore.onEvent(event)
    }

    fun onRecentsEvent(event: RecentsEvent) {
        recentsStore.onEvent(event)
    }
}
