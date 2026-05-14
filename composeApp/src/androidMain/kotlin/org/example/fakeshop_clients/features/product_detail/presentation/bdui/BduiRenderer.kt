package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
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

fun buildActionContext(bindings: Map<String, String>, data: JsonObject): JsonObject =
    buildJsonObject { bindings.forEach { (key, path) -> data.resolve(path)?.let { put(key, it) } } }

@Composable
fun BduiRenderer(
    root: UiNode,
    data: JsonObject,
    modifier: Modifier = Modifier
) {
    RenderNode(root, data, modifier)
}

@Composable
fun RenderNode(node: UiNode, data: JsonObject, modifier: Modifier = Modifier) {
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
