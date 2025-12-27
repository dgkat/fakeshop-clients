package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl(
    private val authRepository: AuthRepository
) : ProductListService {
    override fun getProducts(): List<CategoryRow> {
        return emptyList()
    }

    override suspend fun testApiCalls() {
    }
}