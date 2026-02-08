package org.example.fakeshop_clients.features.home.data.mappers

import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class DataToDomainBriefProductMapper {
    fun map(response: BriefProductResponse): BriefProduct {
        return BriefProduct(
            id = response.id,
            name = response.name,
            price = response.price,
            imageUrl = response.imageUrl,
            category = response.category
        )
    }

    fun map(briefProductResponse: BriefProductsResponse): List<BriefProduct> {
        return briefProductResponse.briefProducts.map {
            map(it)
        }
    }
}
