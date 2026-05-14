package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle

fun FlowContent.renderButton(node: UiNode.Button, data: JsonObject, screen: String) {
    val styleClass = when (node.style) {
        ButtonStyle.primary -> "bdui-btn bdui-btn-primary"
        ButtonStyle.secondary -> "bdui-btn bdui-btn-secondary"
        ButtonStyle.tertiary -> "bdui-btn bdui-btn-tertiary"
    }

    button(classes = styleClass) {
        applyNodeAttrs(node)
        attributes["type"] = "button"

        if (node.actionId.isNotEmpty()) {
            val ctx = buildResolvedContext(node.contextBindings, data)
            attributes["hx-post"] = "/bdui/action"
            attributes["hx-vals"] = buildHxVals(actionId = node.actionId, screen = screen, extra = ctx)
            attributes["hx-swap"] = "none"
        }

        +node.label
    }
}
