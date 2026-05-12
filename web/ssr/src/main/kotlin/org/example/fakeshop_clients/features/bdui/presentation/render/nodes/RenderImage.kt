package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.img
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList

fun FlowContent.renderImage(node: UiNode.Image, data: JsonObject) {
    val url = node.bind?.let { data.resolveString(it) }
    if (url.isNullOrBlank()) {
        div(classes = "bdui-image bdui-image-placeholder") {
            applyNodeAttrs(node)
        }
    } else {
        img(src = url, alt = "", classes = "bdui-image") {
            applyNodeAttrs(node)
        }
    }
}

fun FlowContent.renderImageGallery(node: UiNode.ImageGallery, data: JsonObject) {
    val items = node.bind?.let { data.resolveStringList(it) } ?: emptyList()
    div(classes = "bdui-image-gallery") {
        applyNodeAttrs(node)
        if (items.isEmpty()) {
            div(classes = "bdui-image-placeholder") {}
        } else {
            items.forEach { url ->
                img(src = url, alt = "", classes = "bdui-gallery-item")
            }
        }
    }
}
