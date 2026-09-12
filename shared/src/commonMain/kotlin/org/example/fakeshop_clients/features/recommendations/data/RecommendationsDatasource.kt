package org.example.fakeshop_clients.features.recommendations.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

interface RecommendationsDatasource {
    suspend fun getRecommendations(
        productId: String,
        limit: Int = DEFAULT_LIMIT
    ): Result<BriefProductsResponse, NetworkError>

    companion object {
        const val DEFAULT_LIMIT = 10
    }
}
