package org.example.fakeshop_clients.features.productDetailPage.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProductV2

interface ProductDetailRepository {
    suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProduct, NetworkError>

    suspend fun getDetailedProductV2ById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductV2, NetworkError>

    suspend fun addFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError>

    suspend fun removeFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError>

    suspend fun checkFavorite(
        productId: String,
        cookies: Cookies
    ): Result<Boolean, NetworkError>
}
