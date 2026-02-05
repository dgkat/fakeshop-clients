package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.search.data.models.SearchResponse

class SearchDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : SearchDatasource {

    override suspend fun searchProducts(query: String): Result<SearchResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/search?q=$query")
    }
}
