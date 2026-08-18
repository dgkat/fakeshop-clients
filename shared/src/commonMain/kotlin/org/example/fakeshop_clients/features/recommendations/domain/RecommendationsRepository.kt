package org.example.fakeshop_clients.features.recommendations.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.recommendations.data.RecommendationsDatasource

interface RecommendationsRepository {
    suspend fun getRecommendations(
        productId: String,
        limit: Int = RecommendationsDatasource.DEFAULT_LIMIT
    ): Result<List<BriefProduct>, NetworkError>
}
