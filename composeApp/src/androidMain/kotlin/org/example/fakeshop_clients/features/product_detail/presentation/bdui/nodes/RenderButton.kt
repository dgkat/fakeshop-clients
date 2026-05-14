package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.ButtonStyle
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.LocalBduiActionHandler
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.buildActionContext

@Composable
fun RenderButton(node: UiNode.Button, data: JsonObject) {
    val onAction = LocalBduiActionHandler.current
    val sized = node.alignment != null || node.widthFraction != null
    val modifier = if (sized) Modifier else Modifier.fillMaxWidth()
    val onClick = {
        if (node.actionId.isNotBlank()) {
            onAction(node.actionId, buildActionContext(node.contextBindings, data))
        }
    }

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
