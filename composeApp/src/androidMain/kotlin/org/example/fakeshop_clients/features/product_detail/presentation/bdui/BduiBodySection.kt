package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.features.bdui.presentation.BduiError
import org.example.fakeshop_clients.features.productDetail.presentation.BduiBodyState

@Composable
fun BduiBodySection(state: BduiBodyState, modifier: Modifier = Modifier) {
    when (state) {
        is BduiBodyState.Loading -> BduiLoadingIndicator(modifier)
        is BduiBodyState.Error -> BduiErrorIndicator(state.error, modifier)
        is BduiBodyState.Ready -> BduiRenderer(state.template.root, state.bindData.json, modifier)
    }
}

@Composable
private fun BduiLoadingIndicator(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp))
        Text(
            text = "Loading…",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun BduiErrorIndicator(error: BduiError, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = when (error) {
                    is BduiError.Network -> "Couldn't load product details"
                    BduiError.NotFound -> "Product layout not found"
                    is BduiError.ParseError -> "Unsupported layout"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}
