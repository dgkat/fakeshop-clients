package org.example.fakeshop_clients.features.favorites.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckRequest
import org.example.fakeshop_clients.features.favorites.domain.FavoritesRepository
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class FavoritesRepositoryImpl(
    private val datasource: FavoritesDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper
) : FavoritesRepository {

    override suspend fun addFavorite(productId: String): Result<Unit, NetworkError> {
        return datasource.addFavorite(productId)
    }

    override suspend fun removeFavorite(productId: String): Result<Unit, NetworkError> {
        return datasource.removeFavorite(productId)
    }

    override suspend fun getFavorites(): Result<List<BriefProduct>, NetworkError> {
        return datasource.getFavorites().map { response ->
            briefProductMapper.map(response)
        }
    }

    override suspend fun checkFavorite(productId: String): Result<Boolean, NetworkError> {
        return datasource.checkFavorite(productId).map { it.isFavorited }
    }

    override suspend fun checkBulkFavorites(productIds: List<String>): Result<Set<String>, NetworkError> {
        return datasource.checkBulkFavorites(BulkFavoriteCheckRequest(productIds)).map { response ->
            response.favoritedProductIds.toSet()
        }
    }
}
