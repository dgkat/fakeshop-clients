package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse
import org.example.fakeshop_clients.features.core.models.Cookies

interface SSRBduiActionDatasource {
    suspend fun postAction(
        request: BduiActionRequest,
        cookies: Cookies
    ): Result<BduiActionResponse, NetworkError>
}
