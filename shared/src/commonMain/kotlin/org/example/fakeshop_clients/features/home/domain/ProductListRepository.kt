package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

interface ProductListRepository {
    suspend fun getProductsByCategory(category: String, limit: Int): Result<List<BriefProduct>, NetworkError>
}
