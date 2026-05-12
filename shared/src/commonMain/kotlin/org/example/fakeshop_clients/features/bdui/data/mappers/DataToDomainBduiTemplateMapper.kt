package org.example.fakeshop_clients.features.bdui.data.mappers

import org.example.fakeshop_clients.features.bdui.BduiJson
import org.example.fakeshop_clients.features.bdui.data.models.BduiTemplateResponse
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

class DataToDomainBduiTemplateMapper {
    fun map(response: BduiTemplateResponse): BduiTemplate {
        val root = BduiJson.decodeFromJsonElement(UiNode.serializer(), response.root)
        return BduiTemplate(
            schemaVersion = response.schemaVersion,
            screen = response.screen,
            category = response.category,
            root = root
        )
    }
}
