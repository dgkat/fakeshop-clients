package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RenderSizeSelector(node: UiNode.SizeSelector, data: JsonObject) {
    val sizes: List<String> = node.bind?.let { data.resolveStringList(it) } ?: emptyList()
    var selected by remember { mutableStateOf<String?>(null) }

    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        sizes.forEach { size ->
            FilterChip(
                selected = selected == size,
                onClick = { selected = size },
                label = { Text(size) }
            )
        }
    }
}
