package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.serialization.json.Json.Default.encodeToString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle

fun FlowContent.renderButton(node: UiNode.Button, data: JsonObject, screen: String, productId: String) {
    val styleClass = when (node.style) {
        ButtonStyle.primary -> "bdui-btn bdui-btn-primary"
        ButtonStyle.secondary -> "bdui-btn bdui-btn-secondary"
        ButtonStyle.tertiary -> "bdui-btn bdui-btn-tertiary"
    }

    button(classes = styleClass) {
        applyNodeAttrs(node)
        attributes["type"] = "button"

        when {
            node.actionId == BduiConstants.REPLACE_ACTION_ID -> {
                // `replace` is resolved server-side by /bdui/replace, NOT the /ui/action allowlist.
                // targetSlotId is a verbatim literal in contextBindings, not a bind path.
                val targetSlotId = node.contextBindings[BduiConstants.TARGET_SLOT_ID_KEY]
                if (targetSlotId != null) {
                    attributes["hx-post"] = "/bdui/replace"
                    attributes["hx-vals"] = buildReplaceHxVals(productId, targetSlotId)
                    attributes["hx-target"] = "#$targetSlotId"
                    attributes["hx-swap"] = "outerHTML"
                }
            }

            node.actionId.isNotEmpty() -> {
                val ctx = buildResolvedContext(node.contextBindings, data)
                attributes["hx-post"] = "/bdui/action"
                attributes["hx-vals"] = buildHxVals(actionId = node.actionId, screen = screen, extra = ctx)
                attributes["hx-swap"] = "none"
            }
        }

        +node.label
    }
}

private fun buildReplaceHxVals(productId: String, targetSlotId: String): String {
    val obj = buildJsonObject {
        put("productId", productId)
        put(BduiConstants.TARGET_SLOT_ID_KEY, targetSlotId)
    }
    return encodeToString(JsonObject.serializer(), obj)
}
