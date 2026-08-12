package org.example.fakeshop_clients.features.search_bar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.search.presentation.SearchEvent
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore

class SearchViewModel(
    storeFactory: (CoroutineScope) -> SearchViewStore
) : ViewModel() {
    private val store = storeFactory(viewModelScope)
    val state = store.searchState

    fun onEvent(event: SearchEvent) {
        store.onEvent(event)
    }
}
