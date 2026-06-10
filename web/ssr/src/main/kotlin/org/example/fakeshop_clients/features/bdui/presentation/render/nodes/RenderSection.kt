package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.h2
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.presentation.render.renderBduiNode

fun FlowContent.renderSection(node: UiNode.Section, data: JsonObject, screen: String, productId: String) {
    div(classes = "bdui-section") {
        applyNodeAttrs(node)
        node.title?.takeIf { it.isNotBlank() }?.let { title ->
            h2(classes = "bdui-section-title") { +title }
        }
        node.children.forEach { renderBduiNode(it, data, screen, productId) }
    }
}
