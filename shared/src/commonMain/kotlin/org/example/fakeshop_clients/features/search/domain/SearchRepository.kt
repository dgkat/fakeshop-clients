package org.example.fakeshop_clients.features.search.domain

import org.example.fakeshop_clients.features.search.domain.models.SearchResult

interface SearchRepository {
    suspend fun searchByQuery(query: String): List<SearchResult>
}
