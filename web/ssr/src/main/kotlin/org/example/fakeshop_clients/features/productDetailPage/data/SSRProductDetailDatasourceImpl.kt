package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.data.SSRSafeApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

class SSRProductDetailDatasourceImpl(
    private val safeApiClient: SSRSafeApiClient,
    private val baseUrl: UrlProvider
) : ProductDetailDatasource {

    override suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProductResponse, NetworkError> {
        return safeApiClient.get(
            path = "${baseUrl()}/products/brief/$id",
            cookies = cookies
        )
    }

    override suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductResponse, NetworkError> {
        return safeApiClient.get(
            path = "${baseUrl()}/products/detailed/$id",
            cookies = cookies
        )
    }

    override suspend fun toggleLike(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return safeApiClient.post(
            path = "${baseUrl()}/products/$productId/like",
            cookies = cookies
        )
    }

}