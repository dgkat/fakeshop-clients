package org.example.fakeshop_clients.features.bdui.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse
import org.example.fakeshop_clients.features.core.models.Cookies

interface SSRBduiTemplateDatasource {
    suspend fun getPdpTemplate(
        category: String,
        cookies: Cookies
    ): Result<BduiTemplateResponse, NetworkError>
}
