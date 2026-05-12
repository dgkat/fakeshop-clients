package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.FlowContent
import kotlinx.html.div
import kotlinx.html.hr
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.SpacerSize

fun FlowContent.renderSpacer(node: UiNode.Spacer) {
    val sizeClass = when (node.size) {
        SpacerSize.sm -> "bdui-spacer-sm"
        SpacerSize.md -> "bdui-spacer-md"
        SpacerSize.lg -> "bdui-spacer-lg"
        SpacerSize.xl -> "bdui-spacer-xl"
    }
    div(classes = "bdui-spacer $sizeClass") {}
}

fun FlowContent.renderDivider() {
    hr(classes = "bdui-divider") {}
}
