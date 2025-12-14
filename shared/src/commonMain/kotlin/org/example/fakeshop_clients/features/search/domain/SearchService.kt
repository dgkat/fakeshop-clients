package org.example.fakeshop_clients.features.search.domain

import org.example.fakeshop_clients.features.search.domain.models.SearchResult

interface SearchService {
    suspend fun searchByQuery(query: String): List<SearchResult>
}