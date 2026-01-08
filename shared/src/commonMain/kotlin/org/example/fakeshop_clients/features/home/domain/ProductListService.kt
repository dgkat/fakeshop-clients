package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

interface ProductListService {
    suspend fun getProducts(): Result<List<CategoryRow>, NetworkError>

    suspend fun testApiCalls()
}