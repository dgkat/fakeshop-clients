package org.example.fakeshop_clients.features.bdui.presentation.render.nodes

import kotlinx.html.CommonAttributeGroupFacade
import org.example.fakeshop_clients.features.bdui.domain.BduiReplaceResolver
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.NodeAlignment
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.Spacing

internal fun CommonAttributeGroupFacade.applyNodeAttrs(node: UiNode) {
    // A slotted node's `id` makes it an htmx swap target for the Replace feature. Any node the
    // CMS can author a slotId on is eligible (every type except Divider) via slotIdOf.
    BduiReplaceResolver.slotIdOf(node)?.let { attributes["id"] = it }
    val tokens = mutableListOf<String>()
    node.alignment?.let { tokens += alignmentClass(it) }
    node.widthFraction?.let { tokens += "bdui-w-${(it * 100).toInt()}" }
    node.weight?.let { attributes["style"] = "flex: ${it}; min-width: 0;" }
    if (tokens.isNotEmpty()) {
        val existing = attributes["class"]
        attributes["class"] = if (existing.isNullOrBlank()) tokens.joinToString(" ") else "$existing ${tokens.joinToString(" ")}"
    }
}

internal fun alignmentClass(alignment: NodeAlignment) = when (alignment) {
    NodeAlignment.start -> "bdui-align-start"
    NodeAlignment.center -> "bdui-align-center"
    NodeAlignment.end -> "bdui-align-end"
}

internal fun spacingClass(spacing: Spacing) = when (spacing) {
    Spacing.sm -> "bdui-gap-sm"
    Spacing.md -> "bdui-gap-md"
    Spacing.lg -> "bdui-gap-lg"
    Spacing.xl -> "bdui-gap-xl"
}
