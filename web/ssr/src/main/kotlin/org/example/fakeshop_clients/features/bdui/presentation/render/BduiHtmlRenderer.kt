package org.example.fakeshop_clients.features.bdui.presentation.render

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode

// Per-node renderers will be filled in by Step 2.3.
fun FlowContent.renderBduiNode(node: UiNode, data: JsonObject) {
    div(classes = "bdui-placeholder") {
        +"[BDUI: ${node::class.simpleName}]"
    }
}
