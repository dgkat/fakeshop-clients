package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.button
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle

// v1: navigate actions render as anchors with href; other actions render as buttons carrying
// data-action / data-target attributes. Full htmx-driven action protocol is a future TODO.
fun FlowContent.renderButton(node: UiNode.Button) {
    val styleClass = when (node.style) {
        ButtonStyle.primary -> "bdui-btn bdui-btn-primary"
        ButtonStyle.secondary -> "bdui-btn bdui-btn-secondary"
        ButtonStyle.tertiary -> "bdui-btn bdui-btn-tertiary"
    }

    if (node.action.type == "navigate") {
        a(href = node.action.target ?: "#", classes = styleClass) {
            applyNodeAttrs(node)
            +node.label
        }
    } else {
        button(classes = styleClass) {
            applyNodeAttrs(node)
            attributes["type"] = "button"
            attributes["data-action"] = node.action.type
            node.action.target?.let { attributes["data-target"] = it }
            +node.label
        }
    }
}
