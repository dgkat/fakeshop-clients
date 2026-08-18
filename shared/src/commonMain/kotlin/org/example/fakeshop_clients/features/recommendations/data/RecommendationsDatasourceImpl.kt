package org.example.fakeshop_clients.features.recommendations.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

class RecommendationsDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : RecommendationsDatasource {

    override suspend fun getRecommendations(
        productId: String,
        limit: Int
    ): Result<BriefProductsResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/products/$productId/recommendations?limit=$limit")
    }
}
