package org.example.fakeshop_clients.features.productDetail.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

interface ProductDetailRepository {
    suspend fun getBriefProductById(
        id: String,
        surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
        position: Int? = null
    ): Result<BriefProduct, NetworkError>
    suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError>
}
