package org.example.fakeshop_clients.features.favorites.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

interface FavoritesRepository {
    suspend fun addFavorite(productId: String): Result<Unit, NetworkError>
    suspend fun removeFavorite(productId: String): Result<Unit, NetworkError>
    suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError>
    suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError>
    suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError>
}
