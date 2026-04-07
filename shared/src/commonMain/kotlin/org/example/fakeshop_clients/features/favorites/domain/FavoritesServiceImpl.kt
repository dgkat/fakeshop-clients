package org.example.fakeshop_clients.features.favorites.domain

import kotlinx.coroutines.flow.StateFlow
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.data.FavoritesCache
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class FavoritesServiceImpl(
    private val repository: FavoritesRepository,
    private val cache: FavoritesCache
) : FavoritesService {

    override val favoritedIds: StateFlow<Set<String>> = cache.favoritedIds

    override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> {
        return repository.getFavorites().also { result ->
            result.fold(
                onSuccess = { products -> cache.setIds(products.map { it.id }.toSet()) },
                onError = { }
            )
        }
    }

    override suspend fun toggleFavorite(productId: String, currentlyFavorited: Boolean): Result<Unit, NetworkError> {
        cache.toggle(productId)

        val result = if (currentlyFavorited) {
            repository.removeFavorite(productId)
        } else {
            repository.addFavorite(productId)
        }

        result.fold(
            onSuccess = { },
            onError = { cache.toggle(productId) }
        )

        return result
    }

    override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> {
        return repository.checkFavorite(productId).also { result ->
            result.fold(
                onSuccess = { isFavorited ->
                    if (isFavorited) {
                        cache.addIds(setOf(productId))
                    } else {
                        cache.removeId(productId)
                    }
                },
                onError = { }
            )
        }
    }

    override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> {
        return repository.checkBulkFavorites(productIds).also { result ->
            result.fold(
                onSuccess = { ids -> cache.addIds(ids) },
                onError = { }
            )
        }
    }

    override fun clearCache() {
        cache.clear()
    }
}
