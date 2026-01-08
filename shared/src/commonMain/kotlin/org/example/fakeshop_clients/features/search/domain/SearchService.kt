package org.example.fakeshop_clients.features.search.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.search.domain.models.SearchResult

interface SearchService {
    suspend fun searchByQuery(query: String): Result<List<SearchResult>, NetworkError>
}