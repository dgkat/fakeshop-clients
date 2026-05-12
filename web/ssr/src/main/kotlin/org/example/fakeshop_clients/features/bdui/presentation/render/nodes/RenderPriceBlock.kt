package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.p
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveDouble

fun FlowContent.renderPriceBlock(node: UiNode.PriceBlock, data: JsonObject) {
    val price = node.bind?.let { data.resolveDouble(it) }
    p(classes = "bdui-price") {
        applyNodeAttrs(node)
        +(price?.let { "$${String.format("%.2f", it)}" } ?: "—")
    }
}
