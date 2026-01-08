package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.search.domain.SearchRepository
import org.example.fakeshop_clients.features.search.domain.SearchService
import org.example.fakeshop_clients.features.search.domain.models.SearchResult

class SearchServiceImpl(private val repository: SearchRepository) : SearchService {

    override suspend fun searchByQuery(query: String): Result<List<SearchResult>, NetworkError> {
        return repository.searchByQuery(query)
    }
}