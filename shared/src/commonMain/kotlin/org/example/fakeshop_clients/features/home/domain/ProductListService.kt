package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

interface ProductListService {
    fun getProducts(): List<CategoryRow>

    suspend fun testApiCalls()
}