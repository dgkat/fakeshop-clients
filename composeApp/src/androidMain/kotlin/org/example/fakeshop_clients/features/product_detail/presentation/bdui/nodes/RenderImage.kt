package org.example.fakeshop_clients.features.product_detail.presentation.bdui.nodes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.domain.models.resolveString
import org.example.fakeshop_clients.features.bdui.domain.models.resolveStringList

@Composable
fun RenderImage(node: UiNode.Image, data: JsonObject) {
    val url = node.bind?.let { data.resolveString(it) }
    if (url.isNullOrBlank()) return
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(RoundedCornerShape(8.dp))
    )
}

@Composable
fun RenderImageGallery(node: UiNode.ImageGallery, data: JsonObject) {
    val items: List<String> = node.bind?.let { data.resolveStringList(it) } ?: emptyList()

    if (items.isEmpty()) {
        ImagePlaceholder(label = "gallery (empty)", modifier = Modifier.fillMaxWidth().height(120.dp))
        return
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        items.forEachIndexed { index, url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.weight(1f).aspectRatio(1f).clip(RoundedCornerShape(8.dp))
            )
            if (index < items.lastIndex) Spacer(Modifier.width(4.dp))
        }
    }
}

@Composable
internal fun ImagePlaceholder(label: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
