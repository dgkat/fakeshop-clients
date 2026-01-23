package org.example.fakeshop_clients.features.home.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

interface ProductListDatasource {
    suspend fun getProductsByCategory(category: String, limit: Int = 5): Result<BriefProductsResponse, NetworkError>
}
