package org.example.fakeshop_clients.features.bdui.presentation.render

import kotlinx.html.FlowContent
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
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

fun FlowContent.renderBduiNode(node: UiNode, data: JsonObject) {
    when (node) {
        is UiNode.Column -> renderColumn(node, data)
        is UiNode.Row -> renderRow(node, data)
        is UiNode.Section -> renderSection(node, data)
        is UiNode.Text -> renderText(node, data)
        is UiNode.Image -> renderImage(node, data)
        is UiNode.ImageGallery -> renderImageGallery(node, data)
        is UiNode.PriceBlock -> renderPriceBlock(node, data)
        is UiNode.SpecTable -> renderSpecTable(node, data)
        is UiNode.SizeSelector -> renderSizeSelector(node, data)
        is UiNode.ColorSwatchPicker -> renderColorSwatchPicker(node, data)
        is UiNode.Button -> renderButton(node)
        is UiNode.Spacer -> renderSpacer(node)
        is UiNode.Divider -> renderDivider()
    }
}
