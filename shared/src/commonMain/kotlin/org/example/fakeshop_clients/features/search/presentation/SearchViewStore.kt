package org.example.fakeshop_clients.features.search.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.search.domain.SearchService
import kotlin.time.Duration.Companion.milliseconds

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

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    private fun observeQueryChanges() {
        queryFlow
            .debounce(300.milliseconds)
            .filter { it.length >= 2 || it.isEmpty() }
            // flatMapLatest cancels the previous query's in-flight search when a newer query
            // arrives, so a slow earlier response can't land after (and clobber) a newer one (item 8).
            .flatMapLatest { query -> reduceForQuery(query) }
            .launchIn(scope)
    }

    private fun reduceForQuery(query: String): Flow<Unit> = flow {
        when {
            query.isEmpty() -> {
                _searchState.update { it.copy(results = emptyList(), error = null) }
            }
            query.length < 2 -> {
                _searchState.update {
                    it.copy(results = emptyList(), error = SearchError.TooShort)
                }
            }
            else -> {
                _searchState.update { it.copy(isLoading = true, error = null) }

                searchService.searchByQuery(query).fold(
                    onSuccess = { results ->
                        _searchState.update {
                            it.copy(results = results, isLoading = false, error = null)
                        }
                    },
                    onError = { networkError ->
                        _searchState.update {
                            it.copy(
                                results = emptyList(),
                                isLoading = false,
                                error = SearchError.Network(networkError)
                            )
                        }
                    }
                )
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
        deactivateSearch()
    }

    private fun clearQuery() {
        _searchState.update { it.copy(query = "") }
    }
}