package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.dd
import kotlinx.html.div
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.SpecItem
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveSpecItems

fun FlowContent.renderSpecTable(node: UiNode.SpecTable, data: JsonObject) {
    val items = node.bind?.let { data.resolveSpecItems(it) }?.takeIf { it.isNotEmpty() } ?: run {
        renderEmptySlotAnchor(node)
        return
    }
    dl(classes = "bdui-spec-table") {
        applyNodeAttrs(node)
        items.forEach { item ->
            when (item) {
                is SpecItem.Group -> div(classes = "bdui-spec-group") { +item.title }
                is SpecItem.Row -> {
                    dt(classes = "bdui-spec-label") { +item.label }
                    dd(classes = "bdui-spec-value") { +item.value }
                }
            }
        }
    }
}
