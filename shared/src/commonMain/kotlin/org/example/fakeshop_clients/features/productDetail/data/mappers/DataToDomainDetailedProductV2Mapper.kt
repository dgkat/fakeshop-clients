package org.example.fakeshop_clients.features.productDetail.data.mappers

import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductV2Response
import org.example.fakeshop_clients.features.productDetail.domain.models.DetailedProductV2

class DataToDomainDetailedProductV2Mapper {
    fun map(response: DetailedProductV2Response): DetailedProductV2 {
        return DetailedProductV2(
            productId = response.productId,
            category = response.category,
            fullDescription = response.fullDescription,
            galleryUrls = response.galleryUrls,
            data = response.data
        )
    }
}
