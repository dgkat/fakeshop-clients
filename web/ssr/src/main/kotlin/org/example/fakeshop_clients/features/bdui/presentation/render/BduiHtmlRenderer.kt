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

fun FlowContent.renderBduiNode(node: UiNode, data: JsonObject, screen: String) {
    when (node) {
        is UiNode.Column -> renderColumn(node, data, screen)
        is UiNode.Row -> renderRow(node, data, screen)
        is UiNode.Section -> renderSection(node, data, screen)
        is UiNode.Text -> renderText(node, data)
        is UiNode.Image -> renderImage(node, data)
        is UiNode.ImageGallery -> renderImageGallery(node, data)
        is UiNode.PriceBlock -> renderPriceBlock(node, data)
        is UiNode.SpecTable -> renderSpecTable(node, data)
        is UiNode.SizeSelector -> renderSizeSelector(node, data, screen)
        is UiNode.ColorSwatchPicker -> renderColorSwatchPicker(node, data, screen)
        is UiNode.Button -> renderButton(node, data, screen)
        is UiNode.Spacer -> renderSpacer(node)
        is UiNode.Divider -> renderDivider()
    }
}
