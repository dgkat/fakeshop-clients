package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.dd
import kotlinx.html.dl
import kotlinx.html.dt
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveSpecPairs

fun FlowContent.renderSpecTable(node: UiNode.SpecTable, data: JsonObject) {
    val rows = node.bind?.let { data.resolveSpecPairs(it) }?.takeIf { it.isNotEmpty() } ?: return
    dl(classes = "bdui-spec-table") {
        applyNodeAttrs(node)
        rows.forEach { (label, value) ->
            dt(classes = "bdui-spec-label") { +label }
            dd(classes = "bdui-spec-value") { +value }
        }
    }
}
