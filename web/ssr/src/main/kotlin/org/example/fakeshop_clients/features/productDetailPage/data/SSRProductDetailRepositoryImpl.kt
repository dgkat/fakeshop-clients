package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductV2Mapper
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProductV2
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailRepository

class SSRProductDetailRepositoryImpl(
    private val productDetailDatasource: ProductDetailDatasource,
    private val dataToDomainBriefProductMapper: DataToDomainBriefProductMapper,
    private val dataToDomainDetailedProductV2Mapper: DataToDomainDetailedProductV2Mapper
) : ProductDetailRepository {
    override suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProduct, NetworkError> {
        return productDetailDatasource.getBriefProductById(id, cookies).map {
            dataToDomainBriefProductMapper.map(it)
        }
    }

    override suspend fun getDetailedProductV2ById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductV2, NetworkError> {
        return productDetailDatasource.getDetailedProductV2ById(id, cookies).map {
            dataToDomainDetailedProductV2Mapper.map(it)
        }
    }

    override suspend fun addFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailDatasource.addFavorite(productId, cookies)
    }

    override suspend fun removeFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailDatasource.removeFavorite(productId, cookies)
    }

    override suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError> {
        return productDetailDatasource.checkFavorite(productId, cookies)
    }
}
