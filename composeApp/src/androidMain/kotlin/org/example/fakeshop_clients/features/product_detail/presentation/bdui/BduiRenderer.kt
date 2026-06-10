package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.domain.BduiReplaceResolver
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolve
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.NodeAlignment
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderButton
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderColorSwatchPicker
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderColumn
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderDivider
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderImage
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderImageGallery
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderPriceBlock
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderRow
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderSection
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderSizeSelector
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderSpacer
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderSpecTable
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes.RenderText

val LocalBduiActionHandler = compositionLocalOf<(String, JsonObject) -> Unit> { { _, _ -> } }

/**
 * Replaced slots in scope for the current subtree, keyed by `targetSlotId`. Empty by default
 * and re-scoped to empty inside a replacement so the standalone layout never re-triggers a swap.
 */
val LocalReplacedSlots = compositionLocalOf<Map<String, ResolvedReplace>> { emptyMap() }

fun buildActionContext(bindings: Map<String, String>, data: JsonObject): JsonObject =
    buildJsonObject {
        bindings.forEach { (key, value) ->
            if (key == BduiConstants.TARGET_SLOT_ID_KEY) {
                // `targetSlotId` is authored as a verbatim literal (replace action), not a
                // bind path — pass it through unresolved so the slot swap can find its target.
                put(key, value)
            } else {
                data.resolve(value)?.let { put(key, it) }
            }
        }
    }

@Composable
fun BduiRenderer(
    root: UiNode,
    data: JsonObject,
    replacedSlots: Map<String, ResolvedReplace> = emptyMap(),
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalReplacedSlots provides replacedSlots) {
        RenderNode(root, data, modifier)
    }
}

@Composable
fun RenderNode(node: UiNode, data: JsonObject, modifier: Modifier = Modifier) {
    // Standalone-scope replace: if this container's slot was replaced, render the replacement
    // layout against its own `values` instead of the original subtree + product bindData.
    // Re-scope replacedSlots to empty so the replacement is self-contained (no recursive swap).
    val replacement = BduiReplaceResolver.replacementFor(node, LocalReplacedSlots.current)
    if (replacement != null) {
        CompositionLocalProvider(LocalReplacedSlots provides emptyMap()) {
            RenderNode(replacement.node, replacement.values, modifier)
        }
        return
    }

    val widthFraction = node.widthFraction
    val alignment = node.alignment

    if (alignment != null) {
        val contentAlignment = when (alignment) {
            NodeAlignment.start -> Alignment.CenterStart
            NodeAlignment.center -> Alignment.Center
            NodeAlignment.end -> Alignment.CenterEnd
        }
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = contentAlignment
        ) {
            val innerWidthMod = if (widthFraction != null) Modifier.fillMaxWidth(widthFraction) else Modifier
            Box(modifier = innerWidthMod) {
                RenderNodeContent(node, data)
            }
        }
    } else {
        val widthMod = if (widthFraction != null) Modifier.fillMaxWidth(widthFraction) else Modifier
        Box(modifier = modifier.then(widthMod)) {
            RenderNodeContent(node, data)
        }
    }
}

@Composable
private fun RenderNodeContent(node: UiNode, data: JsonObject) {
    when (node) {
        is UiNode.Column -> RenderColumn(node, data)
        is UiNode.Row -> RenderRow(node, data)
        is UiNode.Section -> RenderSection(node, data)
        is UiNode.Text -> RenderText(node, data)
        is UiNode.Image -> RenderImage(node, data)
        is UiNode.ImageGallery -> RenderImageGallery(node, data)
        is UiNode.PriceBlock -> RenderPriceBlock(node, data)
        is UiNode.SpecTable -> RenderSpecTable(node, data)
        is UiNode.SizeSelector -> RenderSizeSelector(node, data)
        is UiNode.ColorSwatchPicker -> RenderColorSwatchPicker(node, data)
        is UiNode.Button -> RenderButton(node, data)
        is UiNode.Spacer -> RenderSpacer(node)
        is UiNode.Divider -> RenderDivider()
    }
}
