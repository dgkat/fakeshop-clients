package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.Spacing
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.RenderNode

@Composable
fun RenderColumn(node: UiNode.Column, data: JsonObject) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(node.spacing.toDp())
    ) {
        node.children.forEach { child ->
            val w = child.weight
            RenderNode(child, data, modifier = if (w != null) Modifier.weight(w) else Modifier)
        }
    }
}

@Composable
fun RenderRow(node: UiNode.Row, data: JsonObject) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(node.spacing.toDp())
    ) {
        node.children.forEach { child ->
            val w = child.weight
            RenderNode(child, data, modifier = if (w != null) Modifier.weight(w) else Modifier)
        }
    }
}

internal fun Spacing.toDp() = when (this) {
    Spacing.sm -> 4.dp
    Spacing.md -> 8.dp
    Spacing.lg -> 16.dp
    Spacing.xl -> 24.dp
}
