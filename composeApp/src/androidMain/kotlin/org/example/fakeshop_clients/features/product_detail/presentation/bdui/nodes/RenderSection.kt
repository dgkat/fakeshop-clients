package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.RenderNode

@Composable
fun RenderSection(node: UiNode.Section, data: JsonObject) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val title = node.title
        if (!title.isNullOrBlank()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
        }
        node.children.forEach { child ->
            RenderNode(child, data)
        }
    }
}
