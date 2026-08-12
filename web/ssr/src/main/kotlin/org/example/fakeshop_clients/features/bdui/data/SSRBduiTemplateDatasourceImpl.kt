package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.data.SSRSafeApiClient
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse
import org.example.fakeshop_clients.features.core.models.Cookies

class SSRBduiTemplateDatasourceImpl(
    private val safeApiClient: SSRSafeApiClient,
    private val baseUrl: UrlProvider
) : SSRBduiTemplateDatasource {

    override suspend fun getPdpTemplate(
        category: String,
        cookies: Cookies
    ): Result<BduiTemplateResponse, NetworkError> {
        return safeApiClient.get(
            path = "${baseUrl()}/ui/pdp?category=$category",
            cookies = cookies
        )
    }
}
