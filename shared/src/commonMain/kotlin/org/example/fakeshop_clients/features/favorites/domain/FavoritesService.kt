package org.example.fakeshop_clients.features.favorites.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

interface FavoritesService {
    suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError>
    suspend fun toggleFavorite(productId: String, currentlyFavorited: Boolean): Result<Unit, NetworkError>
    suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError>
    suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError>
}
