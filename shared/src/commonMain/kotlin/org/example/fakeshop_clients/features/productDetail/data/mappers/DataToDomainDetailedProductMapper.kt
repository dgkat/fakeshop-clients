package org.example.fakeshop_clients.features.productDetail.data.mappers

import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

class DataToDomainDetailedProductMapper {
    fun map(response: DetailedProductResponse): DetailedProduct {
        return DetailedProduct(
            productId = response.productId,
            category = response.category,
            fullDescription = response.fullDescription,
            galleryUrls = response.galleryUrls,
            data = response.data
        )
    }
}
