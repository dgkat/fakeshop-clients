package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse

interface BduiActionService {
    suspend fun dispatch(
        actionId: String,
        screen: String,
        templateId: String?,
        context: JsonObject,
        idempotencyKey: String? = null
    ): Result<BduiActionResponse, NetworkError>
}
