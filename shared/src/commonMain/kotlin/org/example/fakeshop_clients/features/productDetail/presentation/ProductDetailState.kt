package org.example.fakeshop_clients.features.productDetail.presentation

import kotlinx.serialization.json.JsonObject
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.core.presentation.models.UiDetailedProduct
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.presentation.BduiError

data class ProductDetailState(
    val briefState: BriefProductState = BriefProductState.Loading,
    val detailedState: DetailedProductState = DetailedProductState.Loading,
    val bduiBodyState: BduiBodyState = BduiBodyState.Loading,
    val isFavorited: Boolean = false,
    val isFavoriteLoading: Boolean = false
)

sealed class BriefProductState {
    data object Loading : BriefProductState()
    data class Success(val product: UiBriefProduct) : BriefProductState()
    data class Error(val error: ProductDetailError) : BriefProductState()
}

sealed class DetailedProductState {
    data object Loading : DetailedProductState()
    data class Success(val product: UiDetailedProduct) : DetailedProductState()
    data class Error(val error: ProductDetailError) : DetailedProductState()
}

sealed class BduiBodyState {
    data object Loading : BduiBodyState()
    data class Ready(val template: BduiTemplate, val bindData: JsonObject) : BduiBodyState()
    data class Error(val error: BduiError) : BduiBodyState()
}
