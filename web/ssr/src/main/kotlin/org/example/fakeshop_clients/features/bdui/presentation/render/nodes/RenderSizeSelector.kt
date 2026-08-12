package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList

fun FlowContent.renderSizeSelector(node: UiNode.SizeSelector, data: JsonObject, screen: String) {
    val sizes = node.bind?.let { data.resolveStringList(it) }?.takeIf { it.isNotEmpty() } ?: return
    val selected = data.resolveString("data.selectedSize")
    div(classes = "bdui-size-selector") {
        applyNodeAttrs(node)
        sizes.forEach { size ->
            val isSelected = size == selected
            button(classes = if (isSelected) "bdui-size-chip selected" else "bdui-size-chip") {
                attributes["data-value"] = size
                attributes["type"] = "button"
                attributes["aria-pressed"] = isSelected.toString()

                if (node.actionId.isNotEmpty()) {
                    val ctx = buildResolvedContext(node.contextBindings, data)
                    attributes["hx-post"] = "/bdui/action"
                    attributes["hx-vals"] = buildHxVals(
                        actionId = node.actionId,
                        screen = screen,
                        extra = ctx + ("size" to size)
                    )
                    attributes["hx-swap"] = "none"
                }

                +size
            }
        }
    }
}
