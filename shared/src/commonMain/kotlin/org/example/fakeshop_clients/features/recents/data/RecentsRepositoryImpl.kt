package org.example.fakeshop_clients.features.recents.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.recents.domain.RecentsRepository

class RecentsRepositoryImpl(
    private val datasource: RecentsDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper
) : RecentsRepository {

    override suspend fun getRecentlyViewed(): Result<List<BriefProduct>, NetworkError> {
        return datasource.getRecentlyViewed().map { response ->
            briefProductMapper.map(response)
        }
    }
}
