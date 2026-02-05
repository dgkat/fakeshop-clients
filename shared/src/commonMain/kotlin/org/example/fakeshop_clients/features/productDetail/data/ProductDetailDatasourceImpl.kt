package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

class ProductDetailDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : ProductDetailDatasource {

    override suspend fun getBriefProductById(id: String): Result<BriefProductResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/products/brief/$id")
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProductResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/products/detailed/$id")
    }
}
