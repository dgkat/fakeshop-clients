package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailEvent
import org.example.fakeshop_clients.features.product_detail.presentation.ProductDetailViewModel

@Composable
fun rememberBduiActionHandler(
    viewModel: ProductDetailViewModel
): (String, JsonObject) -> Unit = remember(viewModel) {
    { actionId, context ->
        viewModel.onEvent(ProductDetailEvent.DispatchAction(actionId, context))
    }
}
