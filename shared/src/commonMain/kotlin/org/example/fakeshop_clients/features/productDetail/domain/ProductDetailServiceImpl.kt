package org.example.fakeshop_clients.features.productDetail.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

class ProductDetailServiceImpl(
    private val repository: ProductDetailRepository
) : ProductDetailService {

    override suspend fun getBriefProductById(
        id: String,
        surface: InteractionSurface,
        position: Int?
    ): Result<BriefProduct, NetworkError> {
        return repository.getBriefProductById(id, surface, position)
    }

    override suspend fun getDetailedProductById(id: String): Result<DetailedProduct, NetworkError> {
        return repository.getDetailedProductById(id)
    }
}
