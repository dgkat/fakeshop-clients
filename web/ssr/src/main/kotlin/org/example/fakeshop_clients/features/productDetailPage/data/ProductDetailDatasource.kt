package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

interface ProductDetailDatasource {
    suspend fun getBriefProductById(
        id: String,
        cookies: Cookies,
        interaction: InteractionContext = InteractionContext.None
    ): Result<BriefProductResponse, NetworkError>

    suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductResponse, NetworkError>

    suspend fun addFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext = InteractionContext.None
    ): Result<Unit, NetworkError>

    suspend fun removeFavorite(
        productId: String,
        cookies: Cookies,
        interaction: InteractionContext = InteractionContext.None
    ): Result<Unit, NetworkError>

    suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError>
}
