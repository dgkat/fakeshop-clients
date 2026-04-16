package org.example.fakeshop_clients.features.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class FavoritesViewModel : ViewModel() {

    private val favoritesStore: FavoritesViewStore by lazy {
        getKoin().get<FavoritesViewStore> { parametersOf(viewModelScope) }
    }

    private val recentsStore: RecentsViewStore by lazy {
        getKoin().get<RecentsViewStore> { parametersOf(viewModelScope) }
    }

    val favoritesState = favoritesStore.state
    val recentsState = recentsStore.state

    fun onFavoritesEvent(event: FavoritesEvent) {
        favoritesStore.onEvent(event)
    }

    fun onRecentsEvent(event: RecentsEvent) {
        recentsStore.onEvent(event)
    }
}
