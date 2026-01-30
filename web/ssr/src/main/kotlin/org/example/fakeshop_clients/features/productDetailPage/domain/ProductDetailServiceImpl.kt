package org.example.fakeshop_clients.features.productDetailPage.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.productDetailPage.domain.models.FullProduct

class ProductDetailServiceImpl(
    private val productDetailRepository: ProductDetailRepository,
) : ProductDetailService {


    override suspend fun getFullProductById(
        id: String,
        cookies: Cookies
    ): Result<FullProduct, NetworkError> {
        val briefProduct = productDetailRepository.getBriefProductById(id, cookies)
        val detailedProduct = productDetailRepository.getDetailedProductById(id, cookies)

        return if (briefProduct is Result.Success && detailedProduct is Result.Success) {
            Result.Success(
                FullProduct(
                    id = briefProduct.data.id,
                    name = briefProduct.data.name,
                    price = briefProduct.data.price,
                    imageUrl = briefProduct.data.imageUrl,
                    category = briefProduct.data.category,
                    description = detailedProduct.data.description,
                    specs = detailedProduct.data.specs,
                    galleryUrls = detailedProduct.data.galleryUrls,
                    isLiked = false
                )
            )
        } else {
            val error = when {
                briefProduct is Result.Error -> briefProduct.error
                detailedProduct is Result.Error -> detailedProduct.error
                else -> NetworkError.Unknown(message = "Unknown error")
            }
            Result.Error(error)
        }
    }

    override suspend fun toggleLike(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return productDetailRepository.toggleLike(productId, cookies)

    }

}