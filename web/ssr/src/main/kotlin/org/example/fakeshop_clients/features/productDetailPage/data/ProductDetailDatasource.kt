package org.example.fakeshop_clients.features.productDetailPage.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

interface ProductDetailDatasource {
    suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProductResponse, NetworkError>

    suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductResponse, NetworkError>

    suspend fun toggleLike(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError>
}
