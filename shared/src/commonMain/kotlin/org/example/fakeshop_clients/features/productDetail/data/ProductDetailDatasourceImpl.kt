package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

class ProductDetailDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : ProductDetailDatasource {

    override suspend fun getBriefProductById(
        id: String,
        surface: InteractionSurface,
        position: Int?
    ): Result<BriefProductResponse, NetworkError> {
        return authClient.get(
            path = "${baseUrl()}/products/brief/$id",
            headers = InteractionContext(surface = surface, position = position).toHeaders()
        )
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProductResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/products/v2/detailed/$id")
    }
}
