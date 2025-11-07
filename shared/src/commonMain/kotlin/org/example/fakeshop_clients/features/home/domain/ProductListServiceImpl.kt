package org.example.fakeshop_clients.features.home.domain

import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl: ProductListService {
    override fun getProducts(): List<CategoryRow> {
        return emptyList()
    }
}