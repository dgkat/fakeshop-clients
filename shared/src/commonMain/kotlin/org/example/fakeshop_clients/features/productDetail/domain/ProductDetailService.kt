package org.example.fakeshop_clients.features.productDetail.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProductV2

interface ProductDetailService {
    suspend fun getBriefProductById(id: String): Result<BriefProduct, NetworkError>
    suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError>
    suspend fun getDetailedProductV2ById(id: String): Result<DetailedProductV2, NetworkError>
}
