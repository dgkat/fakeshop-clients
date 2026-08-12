package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.SpacerSize

@Composable
fun RenderSpacer(node: UiNode.Spacer) {
    Spacer(Modifier.height(node.size.toDp()))
}

@Composable
fun RenderDivider() {
    HorizontalDivider()
}

private fun SpacerSize.toDp() = when (this) {
    SpacerSize.sm -> 8.dp
    SpacerSize.md -> 16.dp
    SpacerSize.lg -> 24.dp
    SpacerSize.xl -> 40.dp
}
