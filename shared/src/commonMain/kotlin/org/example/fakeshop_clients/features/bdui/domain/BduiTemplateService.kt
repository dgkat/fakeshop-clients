package org.example.fakeshop_clients.features.bdui.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate

interface BduiTemplateService {
    suspend fun getPdpTemplate(category: String): Result<BduiTemplate, NetworkError>
}
