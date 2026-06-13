package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.bdui.domain.models.ActionContext
import org.example.fakeshop_clients.features.product_detail.presentation.ProductDetailViewModel

@Composable
fun rememberBduiActionHandler(
    viewModel: ProductDetailViewModel
): (String, JsonObject) -> Unit = remember(viewModel) {
    { actionId, context ->
        // Wrap into the cross-platform ActionContext and route through the single dispatch entry
        // point. (On Android the JsonObject is pure-Kotlin, but we keep the same path as iOS.)
        viewModel.dispatchBduiAction(actionId, ActionContext(context))
    }
}
