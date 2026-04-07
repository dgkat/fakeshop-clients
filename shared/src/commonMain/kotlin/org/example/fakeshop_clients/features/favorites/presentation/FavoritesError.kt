package org.example.fakeshop_clients.features.favorites.presentation

import org.example.fakeshop_clients.core.error_handling.NetworkError

sealed interface FavoritesError {
    data class Network(val error: NetworkError) : FavoritesError
}
