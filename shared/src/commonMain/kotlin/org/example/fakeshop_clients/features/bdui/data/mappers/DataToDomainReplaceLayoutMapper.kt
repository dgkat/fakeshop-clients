package org.example.fakeshop_clients.features.bdui.data.mappers

import org.example.fakeshop_clients.features.bdui.BduiJson
import org.example.fakeshop_clients.features.bdui.data.models.ReplaceLayoutResponse
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

/**
 * Decodes a ReplaceLayout's raw `node` JSON into a typed [UiNode] subtree via [BduiJson]
 * (the only `Json` configured with `classDiscriminator = "type"`) — mirrors
 * `DataToDomainBduiTemplateMapper`.
 */
class DataToDomainReplaceLayoutMapper {
    fun map(response: ReplaceLayoutResponse): UiNode =
        BduiJson.decodeFromJsonElement(UiNode.serializer(), response.node)
}
