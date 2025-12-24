package org.example.fakeshop_clients.features.search.presentation

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.StateFlow

class SearchViewModel(
    private val store: SearchViewStore
) {
    val uiState: StateFlow<SearchState> = store.searchState

    private val scope = MainScope()

    fun onEvent(event: SearchEvent) {
        store.onEvent(event)
    }

    fun cleanup() {
        scope.cancel()
    }
}
