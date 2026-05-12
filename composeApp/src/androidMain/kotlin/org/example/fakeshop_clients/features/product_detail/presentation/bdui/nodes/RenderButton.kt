package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.LocalBduiActionHandler

@Composable
fun RenderButton(node: UiNode.Button) {
    val onAction = LocalBduiActionHandler.current
    val sized = node.alignment != null || node.widthFraction != null
    val modifier = if (sized) Modifier else Modifier.fillMaxWidth()
    val onClick = { onAction(node.action) }

    when (node.style) {
        ButtonStyle.primary -> Button(onClick = onClick, modifier = modifier) {
            Text(node.label)
        }
        ButtonStyle.secondary -> OutlinedButton(onClick = onClick, modifier = modifier) {
            Text(node.label)
        }
        ButtonStyle.tertiary -> TextButton(onClick = onClick, modifier = modifier) {
            Text(node.label)
        }
    }
}
