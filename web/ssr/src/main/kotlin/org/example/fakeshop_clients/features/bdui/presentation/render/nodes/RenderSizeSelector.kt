package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList

fun FlowContent.renderSizeSelector(node: UiNode.SizeSelector, data: JsonObject) {
    val sizes = node.bind?.let { data.resolveStringList(it) } ?: emptyList()
    div(classes = "bdui-size-selector") {
        applyNodeAttrs(node)
        sizes.forEach { size ->
            button(classes = "bdui-size-chip") {
                attributes["data-value"] = size
                attributes["type"] = "button"
                +size
            }
        }
    }
}
