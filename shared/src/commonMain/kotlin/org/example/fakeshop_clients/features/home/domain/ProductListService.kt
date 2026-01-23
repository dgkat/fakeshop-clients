package org.example.fakeshop_clients.features.home.domain

import kotlinx.coroutines.flow.Flow
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

interface ProductListService {
    fun getProducts(): Flow<Result<CategoryRow, NetworkError>>
}