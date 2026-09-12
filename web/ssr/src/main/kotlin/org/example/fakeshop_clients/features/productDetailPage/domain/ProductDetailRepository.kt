package org.example.fakeshop_clients.features.productDetailPage.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionContext
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.recommendations.data.RecommendationsDatasource

interface ProductDetailRepository {
    suspend fun getBriefProductById(
        id: String,
        cookies: Cookies,
        interaction: InteractionContext = InteractionContext.None
    ): Result<BriefProduct, NetworkError>

    suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProduct, NetworkError>

    suspend fun getRecommendations(
        productId: String,
        cookies: Cookies,
        limit: Int = RecommendationsDatasource.DEFAULT_LIMIT
    ): Result<List<BriefProduct>, NetworkError>

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
