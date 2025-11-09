package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl(private val apiClient: ApiClient): ProductListService {
    override fun getProducts(): List<CategoryRow> {
        return emptyList()
    }

    override suspend fun testApiCalls() {
        apiClient.testApiCalls()
    }


}