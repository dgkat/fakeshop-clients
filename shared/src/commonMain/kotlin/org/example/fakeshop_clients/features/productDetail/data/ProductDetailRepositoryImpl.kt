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
        return datasource.getBriefProductById(id).map { briefProductMapper.map(it) }
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError> {
        return datasource.getDetailedProductById(id).map { detailedProductMapper.map(it) }
    }
}
