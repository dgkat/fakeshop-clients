package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse

interface BduiActionDatasource {
    suspend fun postAction(
        request: BduiActionRequest,
        idempotencyKey: String? = null
    ): Result<BduiActionResponse, NetworkError>
}
