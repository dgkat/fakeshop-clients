package org.example.fakeshop_clients.features.favorites.domain

import kotlinx.coroutines.flow.StateFlow
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class FavoritesServiceImpl(
    private val repository: FavoritesRepository
) : FavoritesService {

    override val favoritedIds: StateFlow<Set<String>> = repository.favoritedIds

    override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> {
        return repository.getFavorites()
    }

    override suspend fun toggleFavorite(productId: String, currentlyFavorited: Boolean): Result<Unit, NetworkError> {
        return repository.toggleFavorite(productId, currentlyFavorited)
    }

    override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> {
        return repository.checkFavorite(productId)
    }

    override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> {
        return repository.checkBulkFavorites(productIds)
    }

    override fun clearCache() {
        repository.clearCache()
    }
}
