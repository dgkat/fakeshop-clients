package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveColorList

@Composable
fun RenderColorSwatchPicker(node: UiNode.ColorSwatchPicker, data: JsonObject) {
    val entries = node.bind?.let { data.resolveColorList(it) } ?: emptyList()
    val colors: List<Color> = entries.mapNotNull { parseHexColor(it.hex) }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        colors.forEach { color ->
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
            )
        }
    }
}

private fun parseHexColor(hex: String): Color? {
    val stripped = hex.removePrefix("#")
    return try {
        when (stripped.length) {
            6 -> Color(0xFF000000L or stripped.toLong(16))
            8 -> Color(stripped.toLong(16))
            else -> null
        }
    } catch (_: NumberFormatException) {
        null
    }
}
