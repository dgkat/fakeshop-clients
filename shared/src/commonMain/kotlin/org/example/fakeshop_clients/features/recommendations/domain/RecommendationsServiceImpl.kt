package org.example.fakeshop_clients.features.recommendations.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class RecommendationsServiceImpl(
    private val repository: RecommendationsRepository
) : RecommendationsService {

    override suspend fun getRecommendations(
        productId: String,
        limit: Int
    ): Result<List<BriefProduct>, NetworkError> {
        return repository.getRecommendations(productId, limit)
    }
}
