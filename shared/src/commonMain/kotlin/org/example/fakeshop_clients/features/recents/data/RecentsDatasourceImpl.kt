package org.example.fakeshop_clients.features.recents.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

class RecentsDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : RecentsDatasource {

    override suspend fun getRecentlyViewed(): Result<BriefProductsResponse, NetworkError> {
        return authClient.get("${baseUrl()}/recents")
    }
}
