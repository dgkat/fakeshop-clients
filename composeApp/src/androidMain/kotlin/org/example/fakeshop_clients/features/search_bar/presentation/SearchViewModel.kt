package org.example.fakeshop_clients.features.search_bar.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fakeshop_clients.features.search.presentation.SearchEvent
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class SearchViewModel : ViewModel() {
    private val store: SearchViewStore by lazy {
        getKoin().get<SearchViewStore> { parametersOf(viewModelScope) }
    }
    val state = store.searchState

    fun onEvent(event: SearchEvent) {
        store.onEvent(event)
    }
}