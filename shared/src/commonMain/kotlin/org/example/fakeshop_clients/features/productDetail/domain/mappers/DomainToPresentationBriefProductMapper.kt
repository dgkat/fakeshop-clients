package org.example.fakeshop_clients.features.productDetail.domain.mappers

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class DomainToPresentationBriefProductMapper {
    fun map(product: BriefProduct): UiBriefProduct {
        return UiBriefProduct(
            id = product.id,
            name = product.name,
            price = product.price,
            imageUrl = product.imageUrl,
            category = product.category
        )
    }
}
