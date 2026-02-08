package org.example.fakeshop_clients.features.productDetail.domain.mappers

import org.example.fakeshop_clients.core.presentation.models.UiDetailedProduct
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProduct

class DomainToPresentationDetailedProductMapper {
    fun map(product: DetailedProduct): UiDetailedProduct {
        return UiDetailedProduct(
            description = product.description,
            specs = product.specs,
            galleryUrls = product.galleryUrls
        )
    }
}
