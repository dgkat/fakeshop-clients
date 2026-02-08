package org.example.fakeshop_clients.features.home.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

class ProductListDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : ProductListDatasource {

    override suspend fun getProductsByCategory(
        category: String,
        limit: Int
    ): Result<BriefProductsResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/categories/$category?limit=$limit")
    }
}
