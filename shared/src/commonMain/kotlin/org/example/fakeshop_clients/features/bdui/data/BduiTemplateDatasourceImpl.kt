package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse

class BduiTemplateDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : BduiTemplateDatasource {

    override suspend fun getPdpTemplate(category: String): Result<BduiTemplateResponse, NetworkError> {
        return authClient.get(path = "${baseUrl()}/ui/pdp?category=$category")
    }
}
