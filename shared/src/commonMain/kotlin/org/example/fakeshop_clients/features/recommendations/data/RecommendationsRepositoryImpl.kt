package org.example.fakeshop_clients.features.recommendations.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.recommendations.domain.RecommendationsRepository

class RecommendationsRepositoryImpl(
    private val datasource: RecommendationsDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper
) : RecommendationsRepository {

    override suspend fun getRecommendations(
        productId: String,
        limit: Int
    ): Result<List<BriefProduct>, NetworkError> {
        return datasource.getRecommendations(productId, limit).map { briefProductMapper.map(it) }
    }
}
