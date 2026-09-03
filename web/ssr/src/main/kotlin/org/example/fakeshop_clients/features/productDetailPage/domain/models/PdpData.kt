package org.example.fakeshop_clients.features.productDetailPage.domain.models

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

data class PdpData(
    val brief: BriefProduct,
    val galleryUrls: List<String>,
    val body: PdpBody
)

sealed interface PdpBody {
    data class Ready(val template: BduiTemplate, val bindData: JsonObject) : PdpBody

    data class Unavailable(
        val fullDescription: String?,
        val error: NetworkError
    ) : PdpBody
}
