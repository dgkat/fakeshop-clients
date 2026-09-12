package org.example.fakeshop_clients.features.productDetail.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

interface ProductDetailDatasource {
    suspend fun getBriefProductById(
        id: String,
        surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
        position: Int? = null
    ): Result<BriefProductResponse, NetworkError>
    suspend fun getDetailedProductById(id: String): Result<DetailedProductResponse, NetworkError>
}
