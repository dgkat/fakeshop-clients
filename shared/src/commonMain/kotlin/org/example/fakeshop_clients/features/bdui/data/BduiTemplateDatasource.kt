package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse

interface BduiTemplateDatasource {
    suspend fun getPdpTemplate(category: String): Result<BduiTemplateResponse, NetworkError>
}
