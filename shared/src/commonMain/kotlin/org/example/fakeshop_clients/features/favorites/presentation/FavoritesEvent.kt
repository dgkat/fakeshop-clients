package org.example.fakeshop_clients.features.favorites.presentation

sealed class FavoritesEvent {
    data object LoadFavorites : FavoritesEvent()
    data class RemoveFavorite(val productId: String) : FavoritesEvent()
    data object Retry : FavoritesEvent()
}
