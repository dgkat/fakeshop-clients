package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.navigation.AppRouteParser
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.models.NodeAction
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.actionFor
import org.example.fakeshop_clients.features.bdui.domain.models.isSwatchInert
import org.example.fakeshop_clients.features.bdui.domain.models.resolveColorList
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString

fun FlowContent.renderColorSwatchPicker(
    node: UiNode.ColorSwatchPicker,
    data: JsonObject,
    screen: String,
    productId: String,
    locale: String
) {
    val entries = node.bind?.let { data.resolveColorList(it) }?.takeIf { it.isNotEmpty() } ?: return
    val selected = data.resolveString("data.selectedColor")
    div(classes = "bdui-color-swatch-picker") {
        applyNodeAttrs(node)
        entries.forEach { entry ->
            val isSelected = entry.name == selected
            // Re-tap guard: the selected swatch (or one that resolves to no action) is inert.
            val action = if (node.isSwatchInert(entry.name, selected)) null else node.actionFor(entry.name)
            button(classes = if (isSelected) "bdui-color-swatch selected" else "bdui-color-swatch") {
                attributes["data-value"] = entry.name
                attributes["aria-label"] = entry.name
                attributes["type"] = "button"
                attributes["style"] = "background-color: ${entry.hex};"
                if (action == null) {
                    // Selected swatch: keep the highlight but wire nothing so a re-tap can't re-fire.
                    if (isSelected) {
                        attributes["disabled"] = "disabled"
                        attributes["aria-disabled"] = "true"
                    }
                } else {
                    wireSwatchAction(action, entry.name, data, screen, productId, locale)
                }
            }
        }
    }
}

/** Wires the resolved per-swatch [action] onto the current `<button>`, mirroring `wireOnTap`. */
private fun kotlinx.html.BUTTON.wireSwatchAction(
    action: NodeAction,
    colorName: String,
    data: JsonObject,
    screen: String,
    productId: String,
    locale: String
) {
    when (action.actionId) {
        BduiConstants.NAVIGATE_ACTION_ID -> {
            // `url` is a verbatim literal; validate with the shared parser like the other surfaces.
            // Swatches are already <button>, so use the data-href form the delegated click handler reads.
            val url = action.contextBindings[BduiConstants.URL_KEY]
            if (url != null && AppRouteParser.parse(url) != null) {
                attributes["data-href"] = "/$locale$url"
                if (action.contextBindings[BduiConstants.REPLACE_STACK_KEY] == "true") {
                    attributes["data-replace"] = "true"
                }
            }
            // Invalid/unknown destination ⇒ no wiring (safe no-op), swatch stays inert.
        }

        BduiConstants.REPLACE_ACTION_ID -> {
            val targetSlotId = action.contextBindings[BduiConstants.TARGET_SLOT_ID_KEY]
            if (targetSlotId != null) {
                attributes["hx-post"] = "/bdui/replace"
                attributes["hx-vals"] = buildReplaceHxVals(productId, targetSlotId, locale)
                attributes["hx-target"] = "#$targetSlotId"
                attributes["hx-swap"] = "outerHTML"
            }
        }

        else -> {
            if (action.actionId.isBlank()) return
            val ctx = buildResolvedContext(action.contextBindings, data)
            attributes["hx-post"] = "/bdui/action"
            attributes["hx-vals"] = buildHxVals(
                actionId = action.actionId,
                screen = screen,
                extra = ctx + ("color" to colorName)
            )
            attributes["hx-swap"] = "none"
        }
    }
}
