package org.example.fakeshop_clients.features.favorites.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class FavoritesServiceImpl(
    private val repository: FavoritesRepository
) : FavoritesService {

    override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> {
        return repository.getFavorites()
    }

    override suspend fun toggleFavorite(productId: String, currentlyFavorited: Boolean): Result<Unit, NetworkError> {
        return if (currentlyFavorited) {
            repository.removeFavorite(productId)
        } else {
            repository.addFavorite(productId)
        }
    }

    override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> {
        return repository.checkFavorite(productId)
    }

    override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> {
        return repository.checkBulkFavorites(productIds)
    }
}
