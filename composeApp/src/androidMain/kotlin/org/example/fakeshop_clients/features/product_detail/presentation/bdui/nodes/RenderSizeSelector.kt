package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.LocalBduiActionHandler
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.buildActionContext

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RenderSizeSelector(node: UiNode.SizeSelector, data: JsonObject) {
    val sizes: List<String> = node.bind?.let { data.resolveStringList(it) }?.takeIf { it.isNotEmpty() } ?: return
    val selected: String? = data.resolveString("data.selectedSize")
    val onAction = LocalBduiActionHandler.current

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        sizes.forEach { size ->
            FilterChip(
                selected = selected == size,
                onClick = {
                    if (node.actionId.isNotBlank()) {
                        val ctx = buildJsonObject {
                            put("size", size)
                            buildActionContext(node.contextBindings, data).forEach { (k, v) -> put(k, v) }
                        }
                        onAction(node.actionId, ctx)
                    }
                },
                label = { Text(size) }
            )
        }
    }
}
