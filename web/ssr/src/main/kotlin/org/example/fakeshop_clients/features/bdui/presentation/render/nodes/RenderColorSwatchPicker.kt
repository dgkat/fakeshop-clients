package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveColorList

fun FlowContent.renderColorSwatchPicker(node: UiNode.ColorSwatchPicker, data: JsonObject) {
    val entries = node.bind?.let { data.resolveColorList(it) } ?: emptyList()
    div(classes = "bdui-color-swatch-picker") {
        applyNodeAttrs(node)
        entries.forEach { entry ->
            button(classes = "bdui-color-swatch") {
                attributes["data-value"] = entry.name
                attributes["aria-label"] = entry.name
                attributes["type"] = "button"
                attributes["style"] = "background-color: ${entry.hex};"
            }
        }
    }
}
