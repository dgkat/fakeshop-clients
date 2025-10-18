package org.example.fakeshop_clients.island.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// Data class for search state
data class SearchState(
    val searchTerm: String = "",
    val results: List<String> = emptyList()
)

// Store that holds the business logic
class SearchStore {
    private val _uiState = MutableStateFlow<SearchState>(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    fun onSearchCharacterAdded() {
        val currentState = _uiState.value
        val newTerm = currentState.searchTerm + "A"
        val newResults = currentState.results + newTerm

        console.log("[SearchStore] Added character: $newTerm, Results: $newResults")

        _uiState.value = SearchState(
            searchTerm = newTerm,
            results = newResults
        )
    }
}

// ViewModel that exposes the store
class SearchViewmodel {
    val scope = CoroutineScope(Dispatchers.Main + Job())

    private val searchStore: SearchStore by lazy {
        SearchStore()
    }

    val uiState: StateFlow<SearchState> = searchStore.uiState

    fun onSearchButtonClick() {
        console.log("[SearchViewmodel] Search button clicked")
        searchStore.onSearchCharacterAdded()
    }
}