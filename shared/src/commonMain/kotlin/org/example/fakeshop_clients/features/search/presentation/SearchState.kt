package org.example.fakeshop_clients.features.search.presentation

import org.example.fakeshop_clients.features.search.domain.models.SearchResult

data class SearchState(
    val query: String = "",
    val isActive: Boolean = false,
    val results: List<SearchResult> = emptyList(),
    val isLoading: Boolean = false
)