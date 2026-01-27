package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

class ProductDetailRepositoryImpl(
    private val datasource: ProductDetailDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper,
    private val detailedProductMapper: DataToDomainDetailedProductMapper
) : ProductDetailRepository {

    override suspend fun getBriefProductById(id: String): Result<BriefProduct, NetworkError> {
        val response = datasource.getBriefProductById(id)
        return response.map { briefProductResponse ->
            briefProductMapper.map(briefProductResponse)
        }
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError> {
        val response = datasource.getDetailedProductById(id)
        return response.map { detailedProductResponse ->
            detailedProductMapper.map(detailedProductResponse)
        }
    }
}
