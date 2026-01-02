package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl(
    private val authRepository: AuthRepository
) : ProductListService {
    override suspend fun getProducts(): Result<List<CategoryRow>, NetworkError> {
        return Result.Success(emptyList())
    }

    override suspend fun testApiCalls() {
    }
}