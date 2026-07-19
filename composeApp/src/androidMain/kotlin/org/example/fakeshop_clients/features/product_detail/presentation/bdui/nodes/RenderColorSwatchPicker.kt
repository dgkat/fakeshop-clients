package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.actionFor
import org.example.fakeshop_clients.features.bdui.domain.models.isSwatchInert
import org.example.fakeshop_clients.features.bdui.domain.models.resolveColorList
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.LocalBduiActionHandler
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.buildActionContext

@Composable
fun RenderColorSwatchPicker(node: UiNode.ColorSwatchPicker, data: JsonObject) {
    val entries = node.bind?.let { data.resolveColorList(it) }?.takeIf { it.isNotEmpty() } ?: return
    val selectedName: String? = data.resolveString("data.selectedColor")
    val onAction = LocalBduiActionHandler.current

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        entries.forEach { entry ->
            val color = parseHexColor(entry.hex) ?: return@forEach
            val isSelected = selectedName == entry.name
            // Re-tap guard: the selected swatch (or one with no resolved action) is inert.
            val action = if (node.isSwatchInert(entry.name, selectedName)) null else node.actionFor(entry.name)
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(color)
                    .then(
                        if (isSelected) Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                        else Modifier.border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                    )
                    .then(
                        if (action != null) {
                            Modifier.clickable {
                                val ctx = buildJsonObject {
                                    put("color", entry.name)
                                    buildActionContext(action.contextBindings, data).forEach { (k, v) -> put(k, v) }
                                }
                                onAction(action.actionId, ctx)
                            }
                        } else {
                            Modifier
                        }
                    )
                    .padding(if (isSelected) 2.dp else 0.dp)
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
