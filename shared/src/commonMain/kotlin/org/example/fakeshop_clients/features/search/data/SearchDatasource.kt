package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.features.search.data.models.SearchResponse

interface SearchDatasource {
    suspend fun searchProducts(query: String): SearchResponse
}
