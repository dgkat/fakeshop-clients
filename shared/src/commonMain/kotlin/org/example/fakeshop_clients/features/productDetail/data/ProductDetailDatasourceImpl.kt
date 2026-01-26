package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

class ProductDetailDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient
) : ProductDetailDatasource {

    override suspend fun getBriefProductById(id: String): Result<BriefProductResponse, NetworkError> {
        return authClient.get(path = "/api/products/brief/$id")
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProductResponse, NetworkError> {
        return authClient.get(path = "/api/products/detailed/$id")
    }
}
