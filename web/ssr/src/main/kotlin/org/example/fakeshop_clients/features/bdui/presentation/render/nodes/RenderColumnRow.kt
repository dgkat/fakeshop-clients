package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.presentation.render.renderBduiNode

fun FlowContent.renderColumn(node: UiNode.Column, data: JsonObject, screen: String, productId: String, locale: String) {
    div(classes = "bdui-column ${spacingClass(node.spacing)}") {
        applyNodeAttrs(node)
        node.children.forEach { renderBduiNode(it, data, screen, productId, locale) }
    }
}

fun FlowContent.renderRow(node: UiNode.Row, data: JsonObject, screen: String, productId: String, locale: String) {
    div(classes = "bdui-row ${spacingClass(node.spacing)}") {
        applyNodeAttrs(node)
        node.children.forEach { renderBduiNode(it, data, screen, productId, locale) }
    }
}
