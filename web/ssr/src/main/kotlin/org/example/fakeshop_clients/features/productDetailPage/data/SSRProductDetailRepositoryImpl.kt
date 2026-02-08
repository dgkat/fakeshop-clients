package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailRepository

class SSRProductDetailRepositoryImpl(
    private val productDetailDatasource: ProductDetailDatasource,
    private val dataToDomainBriefProductMapper: DataToDomainBriefProductMapper,
    private val dataToDomainDetailedProductMapper: DataToDomainDetailedProductMapper

) : ProductDetailRepository {
    override suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProduct, NetworkError> {

        return productDetailDatasource.getBriefProductById(id, cookies).map {
            dataToDomainBriefProductMapper.map(it)
        }
    }

    override suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProduct, NetworkError> {
        return productDetailDatasource.getDetailedProductById(id, cookies).map {
            dataToDomainDetailedProductMapper.map(it)
        }
    }

    override suspend fun toggleLike(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailDatasource.toggleLike(productId, cookies)
    }
}