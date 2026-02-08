package org.example.fakeshop_clients.features.productDetail.data.mappers

import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

class DataToDomainDetailedProductMapper {
    fun map(response: DetailedProductResponse): DetailedProduct {
        return DetailedProduct(
            description = response.description,
            specs = response.specs,
            galleryUrls = response.galleryUrls
        )
    }
}
