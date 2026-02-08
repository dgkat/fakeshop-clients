package org.example.fakeshop_clients.features.home.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.ProductListRepository
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class ProductListRepositoryImpl(
    private val datasource: ProductListDatasource,
    private val mapper: DataToDomainBriefProductMapper
) : ProductListRepository {
    override suspend fun getProductsByCategory(
        category: String,
        limit: Int
    ): Result<List<BriefProduct>, NetworkError> {
        val response = datasource.getProductsByCategory(category, limit)

        return response.map { briefProductsResponse ->
            mapper.map(briefProductsResponse)
        }
    }
}