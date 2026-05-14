package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveColorList
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString

fun FlowContent.renderColorSwatchPicker(node: UiNode.ColorSwatchPicker, data: JsonObject, screen: String) {
    val entries = node.bind?.let { data.resolveColorList(it) } ?: emptyList()
    val selected = data.resolveString("data.selectedColor")
    div(classes = "bdui-color-swatch-picker") {
        applyNodeAttrs(node)
        entries.forEach { entry ->
            val isSelected = entry.name == selected
            button(classes = if (isSelected) "bdui-color-swatch selected" else "bdui-color-swatch") {
                attributes["data-value"] = entry.name
                attributes["aria-label"] = entry.name
                attributes["type"] = "button"
                attributes["style"] = "background-color: ${entry.hex};"

                if (node.actionId.isNotEmpty()) {
                    val ctx = buildResolvedContext(node.contextBindings, data)
                    attributes["hx-post"] = "/bdui/action"
                    attributes["hx-vals"] = buildHxVals(
                        actionId = node.actionId,
                        screen = screen,
                        extra = ctx + ("color" to entry.name)
                    )
                    attributes["hx-swap"] = "none"
                }
            }
        }
    }
}
