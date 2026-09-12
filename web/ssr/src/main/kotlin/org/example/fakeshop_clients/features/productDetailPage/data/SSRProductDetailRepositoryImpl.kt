package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.recommendations.data.RecommendationsDatasource

class SSRProductDetailRepositoryImpl(
    private val productDetailDatasource: ProductDetailDatasource,
    private val dataToDomainBriefProductMapper: DataToDomainBriefProductMapper,
    private val dataToDomainDetailedProductMapper: DataToDomainDetailedProductMapper
) : ProductDetailRepository {
    override suspend fun getBriefProductById(
        id: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<BriefProduct, NetworkError> {
        return productDetailDatasource.getBriefProductById(id, cookies, interaction).map {
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

    override suspend fun getRecommendations(
        productId: String,
        cookies: Cookies,
        limit: Int
    ): Result<List<BriefProduct>, NetworkError> {
        return productDetailDatasource.getRecommendations(productId, cookies, limit).map {
            dataToDomainBriefProductMapper.map(it)
        }
    }

    override suspend fun addFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<Unit, NetworkError> {
        return productDetailDatasource.addFavorite(productId, cookies, interaction)
    }

    override suspend fun removeFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext
    ): Result<Unit, NetworkError> {
        return productDetailDatasource.removeFavorite(productId, cookies, interaction)
    }

    override suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError> {
        return productDetailDatasource.checkFavorite(productId, cookies)
    }
}
