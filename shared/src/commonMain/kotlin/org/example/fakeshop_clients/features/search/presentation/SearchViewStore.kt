package org.example.fakeshop_clients.features.search.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.features.search.domain.SearchService

class SearchViewStore(
    private val scope: CoroutineScope,
    private val searchService: SearchService
) {

    private val _searchState = MutableStateFlow(SearchState())
    val searchState: StateFlow<SearchState> = _searchState.asStateFlow()

    private val queryFlow = MutableStateFlow("")

    init {
        observeQueryChanges()
    }

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> onQueryChanged(event.query)
            SearchEvent.CancelClicked -> deactivateSearch()
            SearchEvent.SearchClicked -> activateSearch()
            is SearchEvent.ProductClicked -> onProductClicked(event.productId)
            SearchEvent.ClearQuery -> clearQuery()
        }
    }

    @OptIn(FlowPreview::class)
    private fun observeQueryChanges() {
        scope.launch {
            queryFlow
                .debounce(300)
                .filter { it.length >= 2 || it.isEmpty() }
                .collect { query ->
                    if (query.isEmpty()) {
                        _searchState.update { it.copy(results = emptyList()) }
                    } else {
                        val searchResults = searchService.searchByQuery(query)
                        _searchState.update { it.copy(results = searchResults) }
                    }
                }
        }
    }

    private fun onQueryChanged(query: String) {
        _searchState.update { it.copy(query = query, isActive = true) }
        queryFlow.update { query }
    }

    private fun deactivateSearch() {
        _searchState.update { it.copy(isActive = false) }
    }

    private fun activateSearch() {
        _searchState.update { it.copy(isActive = true) }
    }

    private fun onProductClicked(productId: String) {
        //TODO navigate to product details
        println("Product clicked: $productId")
        deactivateSearch()
    }

    private fun clearQuery() {
        _searchState.update { it.copy(query = "") }
    }
}