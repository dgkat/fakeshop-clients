package org.example.fakeshop_clients.features.bdui.presentation.render

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.navigation.AppRouteParser
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.models.NodeAction
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.buildHxVals
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.buildReplaceHxVals
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.buildResolvedContext
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderButton
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderColorSwatchPicker
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderColumn
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderDivider
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderImage
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderImageGallery
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderPriceBlock
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderRow
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderSection
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderSizeSelector
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderSpacer
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderSpecTable
import org.example.fakeshop_clients.features.bdui.presentation.render.nodes.renderText

fun FlowContent.renderBduiNode(node: UiNode, data: JsonObject, screen: String, productId: String, locale: String) {
    val onTap = node.onTap
    if (onTap != null && renderTappable(node, onTap, data, screen, productId, locale)) return
    renderNodeContent(node, data, screen, productId, locale)
}

/**
 * Wraps a node carrying `onTap`. Returns false when the action can't be rendered
 * (unknown/invalid destination) — the caller then renders the node untouched, so a bad
 * template never breaks the page (safe no-op, same philosophy as replace drift).
 */
private fun FlowContent.renderTappable(
    node: UiNode,
    onTap: NodeAction,
    data: JsonObject,
    screen: String,
    productId: String,
    locale: String
): Boolean {
    when (onTap.actionId) {
        BduiConstants.NAVIGATE_ACTION_ID -> {
            // `url` is a verbatim literal; validate with the shared parser like mobile.
            val url = onTap.contextBindings[BduiConstants.URL_KEY] ?: return false
            AppRouteParser.parse(url) ?: return false
            val href = "/$locale$url"
            val replaceEntry = onTap.contextBindings[BduiConstants.REPLACE_STACK_KEY] == "true"
            if (!replaceEntry && !node.hasInteractiveContent()) {
                // Crawlable real link. Nested interactive HTML (a/button inside a) is
                // invalid, so subtrees containing those fall through to the div form.
                a(href = href, classes = "bdui-tappable") {
                    renderNodeContent(node, data, screen, productId, locale)
                }
            } else {
                // Delegated click handler on the page navigates via data-href;
                // data-replace swaps the history entry instead of pushing.
                div(classes = "bdui-tappable") {
                    attributes["data-href"] = href
                    if (replaceEntry) attributes["data-replace"] = "true"
                    renderNodeContent(node, data, screen, productId, locale)
                }
            }
        }

        BduiConstants.REPLACE_ACTION_ID -> {
            val targetSlotId = onTap.contextBindings[BduiConstants.TARGET_SLOT_ID_KEY] ?: return false
            div(classes = "bdui-tappable") {
                attributes["hx-post"] = "/bdui/replace"
                attributes["hx-vals"] = buildReplaceHxVals(productId, targetSlotId, locale)
                attributes["hx-target"] = "#$targetSlotId"
                attributes["hx-swap"] = "outerHTML"
                renderNodeContent(node, data, screen, productId, locale)
            }
        }

        else -> {
            if (onTap.actionId.isBlank()) return false
            div(classes = "bdui-tappable") {
                attributes["hx-post"] = "/bdui/action"
                attributes["hx-vals"] = buildHxVals(
                    actionId = onTap.actionId,
                    screen = screen,
                    extra = buildResolvedContext(onTap.contextBindings, data)
                )
                attributes["hx-swap"] = "none"
                renderNodeContent(node, data, screen, productId, locale)
            }
        }
    }
    return true
}

/**
 * True when this subtree renders interactive HTML (buttons, or descendants with their own
 * tap wrapper) — content that must not be nested inside an `<a>`.
 */
private fun UiNode.hasInteractiveContent(): Boolean {
    if (this is UiNode.Button || this is UiNode.SizeSelector || this is UiNode.ColorSwatchPicker) return true
    return childNodes().any { it.onTap != null || it.hasInteractiveContent() }
}

private fun UiNode.childNodes(): List<UiNode> = when (this) {
    is UiNode.Column -> children
    is UiNode.Row -> children
    is UiNode.Section -> children
    else -> emptyList()
}

private fun FlowContent.renderNodeContent(node: UiNode, data: JsonObject, screen: String, productId: String, locale: String) {
    when (node) {
        is UiNode.Column -> renderColumn(node, data, screen, productId, locale)
        is UiNode.Row -> renderRow(node, data, screen, productId, locale)
        is UiNode.Section -> renderSection(node, data, screen, productId, locale)
        is UiNode.Text -> renderText(node, data)
        is UiNode.Image -> renderImage(node, data)
        is UiNode.ImageGallery -> renderImageGallery(node, data)
        is UiNode.PriceBlock -> renderPriceBlock(node, data)
        is UiNode.SpecTable -> renderSpecTable(node, data)
        is UiNode.SizeSelector -> renderSizeSelector(node, data, screen)
        is UiNode.ColorSwatchPicker -> renderColorSwatchPicker(node, data, screen, productId, locale)
        is UiNode.Button -> renderButton(node, data, screen, productId, locale)
        is UiNode.Spacer -> renderSpacer(node)
        is UiNode.Divider -> renderDivider()
    }
}
