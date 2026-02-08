package org.example.fakeshop_clients.features.home.domain.mappers

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

    fun map(products: List<BriefProduct>): List<UiBriefProduct> {
        return products.map { map(it) }
    }
}
