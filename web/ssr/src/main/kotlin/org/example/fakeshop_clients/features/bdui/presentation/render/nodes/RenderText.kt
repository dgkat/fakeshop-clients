package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.p
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.TextStyle

fun FlowContent.renderText(node: UiNode.Text, data: JsonObject) {
    val value = node.bind?.let { data.resolveString(it) }
    if (value.isNullOrBlank()) return
    val styleClass = when (node.style) {
        TextStyle.title -> "bdui-text-title"
        TextStyle.subtitle -> "bdui-text-subtitle"
        TextStyle.body -> "bdui-text-body"
        TextStyle.caption -> "bdui-text-caption"
    }
    p(classes = "bdui-text $styleClass") {
        applyNodeAttrs(node)
        +value
    }
}
