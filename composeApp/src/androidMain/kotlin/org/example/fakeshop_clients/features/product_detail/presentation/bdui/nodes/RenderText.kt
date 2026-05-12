package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.NodeAlignment
import org.example.fakeshop_clients.features.bdui.domain.models.tokens.TextStyle

@Composable
fun RenderText(node: UiNode.Text, data: JsonObject) {
    val value = node.bind?.let { data.resolveString(it) } ?: ""
    val textStyle = when (node.style) {
        TextStyle.title -> MaterialTheme.typography.headlineSmall
        TextStyle.subtitle -> MaterialTheme.typography.titleMedium
        TextStyle.body -> MaterialTheme.typography.bodyMedium
        TextStyle.caption -> MaterialTheme.typography.bodySmall
    }
    val textAlign = when (node.alignment) {
        NodeAlignment.start -> TextAlign.Start
        NodeAlignment.center -> TextAlign.Center
        NodeAlignment.end -> TextAlign.End
        null -> null
    }
    Text(
        text = value,
        style = textStyle,
        textAlign = textAlign,
        modifier = if (textAlign != null) Modifier.fillMaxWidth() else Modifier
    )
}
