package org.example.fakeshop_clients.features.home.data.mappers

import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.home.data.models.RemoteBriefProduct

class RemoteBriefProductMapper {
    fun map(remoteBriefProduct: RemoteBriefProduct): UiBriefProduct {
        return UiBriefProduct(
            id = remoteBriefProduct.id,
            name = remoteBriefProduct.name,
            price = remoteBriefProduct.price,
            imageUrl = remoteBriefProduct.imageUrl,
            category = remoteBriefProduct.category
        )
    }

    fun map(remoteBriefProducts: List<RemoteBriefProduct>): List<UiBriefProduct> {
        return remoteBriefProducts.map { map(it) }
    }
}
