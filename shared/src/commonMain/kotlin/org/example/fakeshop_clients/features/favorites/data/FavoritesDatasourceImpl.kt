package org.example.fakeshop_clients.features.favorites.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.deleteNoContent
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.data.postNoContent
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckRequest
import org.example.fakeshop_clients.features.favorites.data.models.BulkFavoriteCheckResponse
import org.example.fakeshop_clients.features.favorites.data.models.FavoriteCheckResponse
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

class FavoritesDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : FavoritesDatasource {

    override suspend fun addFavorite(productId: String): Result<Unit, NetworkError> {
        return authClient.postNoContent("${baseUrl()}/favorites/$productId", "")
    }

    override suspend fun removeFavorite(productId: String): Result<Unit, NetworkError> {
        return authClient.deleteNoContent("${baseUrl()}/favorites/$productId")
    }

    override suspend fun getFavorites(): Result<BriefProductsResponse, NetworkError> {
        return authClient.get("${baseUrl()}/favorites")
    }

    override suspend fun checkFavorite(productId: String): Result<FavoriteCheckResponse, NetworkError> {
        return authClient.get("${baseUrl()}/favorites/check?productId=$productId")
    }

    override suspend fun checkBulkFavorites(request: BulkFavoriteCheckRequest): Result<BulkFavoriteCheckResponse, NetworkError> {
        return authClient.post("${baseUrl()}/favorites/check-bulk", request)
    }
}
