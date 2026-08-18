package org.example.fakeshop_clients.features.productDetail.presentation

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.bdui.domain.models.BindData
import org.example.fakeshop_clients.features.bdui.domain.models.BduiTemplate
import org.example.fakeshop_clients.features.bdui.domain.models.ReplaceBinding
import org.example.fakeshop_clients.features.bdui.domain.models.ResolvedReplace
import org.example.fakeshop_clients.features.bdui.presentation.BduiError

data class ProductDetailState(
    val briefState: BriefProductState = BriefProductState.Loading,
    val bduiBodyState: BduiBodyState = BduiBodyState.Loading,
    val galleryUrls: List<String> = emptyList(),
    val isFavorited: Boolean = false,
    val isFavoriteLoading: Boolean = false,
    val recommendations: List<UiBriefProduct> = emptyList()
)

sealed class BriefProductState {
    data object Loading : BriefProductState()
    data class Success(val product: UiBriefProduct) : BriefProductState()
    data class Error(val error: ProductDetailError) : BriefProductState()
}

sealed class BduiBodyState {
    data object Loading : BduiBodyState()

    /**
     * The rendered PDP body.
     *
     * @param replaceBindings the product's replace wiring, filled in by the 4th non-blocking
     *   PDP call. Empty until it returns (or stays empty if the product has none).
     * @param replacedSlots resolved replacements keyed by `targetSlotId`. Renderers consult
     *   this (via `BduiReplaceResolver`) to swap a slot's subtree for `ResolvedReplace.node`
     *   bound against its own standalone `values`. One-way: once added, stays until reload.
     */
    data class Ready(
        val template: BduiTemplate,
        val bindData: BindData,
        val replaceBindings: List<ReplaceBinding> = emptyList(),
        val replacedSlots: Map<String, ResolvedReplace> = emptyMap()
    ) : BduiBodyState()

    data class Error(val error: BduiError) : BduiBodyState()
}
