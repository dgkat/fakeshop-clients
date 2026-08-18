package org.example.fakeshop_clients.features.favorites.domain

import kotlinx.coroutines.flow.StateFlow
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

interface FavoritesService {
    val favoritedIds: StateFlow<Set<String>>
    suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError>
    suspend fun toggleFavorite(
        productId: String,
        currentlyFavorited: Boolean,
        surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
        position: Int? = null
    ): Result<Unit, NetworkError>
    suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError>
    suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError>
    fun clearCache()
}
