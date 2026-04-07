package org.example.fakeshop_clients.features.favorites.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckRequest
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckResponse
import org.example.fakeshop_clients.features.favorites.data.models.FavoriteCheckResponse
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

interface FavoritesDatasource {
    suspend fun addFavorite(productId: String): Result<Unit, NetworkError>
    suspend fun removeFavorite(productId: String): Result<Unit, NetworkError>
    suspend fun getFavorites(): Result<BriefProductsResponse, NetworkError>
    suspend fun checkFavorite(productId: String): Result<FavoriteCheckResponse, NetworkError>
    suspend fun checkBulkFavorites(request: BulkFavoriteCheckRequest): Result<BulkFavoriteCheckResponse, NetworkError>
}
