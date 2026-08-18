package org.example.fakeshop_clients.features.productDetailPage.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.productDetailPage.domain.models.PdpData

interface ProductDetailService {
    suspend fun getPdpData(
        id: String,
        cookies: Cookies,
        interaction: InteractionContext = InteractionContext.None
    ): Result<PdpData, NetworkError>

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
