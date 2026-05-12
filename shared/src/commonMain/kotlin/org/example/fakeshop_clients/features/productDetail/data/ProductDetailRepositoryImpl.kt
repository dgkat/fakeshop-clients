package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductV2Mapper
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProductV2

class ProductDetailRepositoryImpl(
    private val datasource: ProductDetailDatasource,
    private val briefProductMapper: DataToDomainBriefProductMapper,
    private val detailedProductMapper: DataToDomainDetailedProductMapper,
    private val detailedProductV2Mapper: DataToDomainDetailedProductV2Mapper
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

    override suspend fun getDetailedProductV2ById(id: String): Result<DetailedProductV2, NetworkError> {
        return datasource.getDetailedProductV2ById(id).map { detailedProductV2Mapper.map(it) }
    }
}
