package org.example.fakeshop_clients.features.bdui.domain

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.data.BduiActionDatasource
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionResponse
import org.example.fakeshop_clients.features.bdui.BduiConstants

class BduiActionServiceImpl(
    private val datasource: BduiActionDatasource
) : BduiActionService {

    override suspend fun dispatch(
        actionId: String,
        screen: String,
        templateId: String?,
        context: JsonObject,
        idempotencyKey: String?
    ): Result<BduiActionResponse, NetworkError> {
        val request = BduiActionRequest(
            schemaVersion = BduiConstants.CURRENT_SCHEMA_VERSION,
            actionId = actionId,
            screen = screen,
            templateId = templateId,
            context = context
        )
        return datasource.postAction(request, idempotencyKey)
    }
}
