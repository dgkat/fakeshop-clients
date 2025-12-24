package org.example.fakeshop_clients.features.search.presentation

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
    data object SearchClicked : SearchEvent()
    data object CancelClicked : SearchEvent()
    data class ProductClicked(val productId: String) : SearchEvent()
    data object ClearQuery : SearchEvent()
}