package org.example.fakeshop_clients.features.search.presentation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SearchState(
    val searchTerm: String = "",
    val searchResults: List<String> = emptyList()
)

class SearchStore(private val scope: CoroutineScope) {
    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    fun onSearchClick() {
        val currentState = _uiState.value
        val nextLetter = when {
            currentState.searchTerm.isEmpty() -> "a"
            currentState.searchTerm.length >= 26 -> "a" // Reset after z
            else -> ('a' + currentState.searchTerm.length).toString()
        }

        val newSearchTerm = currentState.searchTerm + nextLetter
        val newResults = currentState.searchResults + newSearchTerm

        _uiState.value = SearchState(
            searchTerm = newSearchTerm,
            searchResults = newResults
        )

        console.log("Search updated: $newSearchTerm, results: $newResults")
    }
}

class SearchViewModel {
    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private val searchStore: SearchStore by lazy {
        SearchStore(scope)
    }

    val uiState: StateFlow<SearchState> = searchStore.uiState

    fun onSearchClick() {
        searchStore.onSearchClick()
    }
}