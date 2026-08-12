package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.postWithHeaders
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse

class BduiActionDatasourceImpl(
    private val authClient: SafeAuthenticatedApiClient,
    private val baseUrl: UrlProvider
) : BduiActionDatasource {

    override suspend fun postAction(
        request: BduiActionRequest,
        idempotencyKey: String?
    ): Result<BduiActionResponse, NetworkError> {
        val path = "${baseUrl()}/ui/action"
        return if (!idempotencyKey.isNullOrBlank()) {
            authClient.postWithHeaders(path, request, mapOf("Idempotency-Key" to idempotencyKey))
        } else {
            authClient.post(path, request)
        }
    }
}
