package org.example.fakeshop_clients.features.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore

class FavoritesViewModel(
    favoritesStoreFactory: (CoroutineScope) -> FavoritesViewStore,
    recentsStoreFactory: (CoroutineScope) -> RecentsViewStore
) : ViewModel() {

    private val favoritesStore = favoritesStoreFactory(viewModelScope)
    private val recentsStore = recentsStoreFactory(viewModelScope)

    val favoritesState = favoritesStore.state
    val recentsState = recentsStore.state

    fun onFavoritesEvent(event: FavoritesEvent) {
        favoritesStore.onEvent(event)
    }

    fun onRecentsEvent(event: RecentsEvent) {
        recentsStore.onEvent(event)
    }
}
