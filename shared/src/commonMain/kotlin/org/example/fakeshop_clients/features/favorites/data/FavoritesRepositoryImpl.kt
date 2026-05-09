package org.example.fakeshop_clients.features.favorites.data

import kotlinx.coroutines.flow.StateFlow
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckRequest
import org.example.fakeshop_clients.features.favorites.domain.FavoritesRepository
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class FavoritesRepositoryImpl(
    private val datasource: FavoritesDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper,
    private val cache: FavoritesCache
) : FavoritesRepository {

    override val favoritedIds: StateFlow<Set<String>> = cache.favoritedIds

    override suspend fun addFavorite(productId: String): Result<Unit, NetworkError> {
        return datasource.addFavorite(productId)
    }

    override suspend fun removeFavorite(productId: String): Result<Unit, NetworkError> {
        return datasource.removeFavorite(productId)
    }

    override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> {
        return datasource.getFavorites().map { response ->
            briefProductMapper.map(response)
        }.also { result ->
            result.fold(
                onSuccess = { products -> cache.setIds(products.map { it.id }.toSet()) },
                onError = { }
            )
        }
    }

    override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> {
        return datasource.checkFavorite(productId).map { it.isFavorited }.also { result ->
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
        return datasource.checkBulkFavorites(BulkFavoriteCheckRequest(productIds)).map { response ->
            response.favoritedProductIds.toSet()
        }.also { result ->
            result.fold(
                onSuccess = { ids -> cache.setIds(ids) },
                onError = { }
            )
        }
    }

    override suspend fun toggleFavorite(productId: String, currentlyFavorited: Boolean): Result<Unit, NetworkError> {
        cache.toggle(productId)

        val result = if (currentlyFavorited) {
            removeFavorite(productId)
        } else {
            addFavorite(productId)
        }

        result.fold(
            onSuccess = { },
            onError = { cache.toggle(productId) }
        )

        return result
    }

    override fun clearCache() {
        cache.clear()
    }
}
