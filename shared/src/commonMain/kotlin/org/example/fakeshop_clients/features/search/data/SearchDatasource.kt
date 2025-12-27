package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.search.data.models.SearchResponse

interface SearchDatasource {
    suspend fun searchProducts(query: String): Result<SearchResponse, NetworkError>
}
