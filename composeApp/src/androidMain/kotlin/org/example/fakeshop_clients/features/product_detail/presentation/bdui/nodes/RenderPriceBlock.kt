package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveDouble

@Composable
fun RenderPriceBlock(node: UiNode.PriceBlock, data: JsonObject) {
    val price = node.bind?.let { data.resolveDouble(it) }

    Column {
        if (price != null) {
            Text(
                text = "$${String.format("%.2f", price)}",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = "—",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
