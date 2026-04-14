package org.example.fakeshop_clients.features.favorites.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class FavoritesViewModel : ViewModel() {

    private val profileService: ProfileService by lazy { getKoin().get() }

    private val favoritesStore: FavoritesViewStore by lazy {
        getKoin().get<FavoritesViewStore> { parametersOf(viewModelScope) }
    }

    private val recentsStore: RecentsViewStore by lazy {
        getKoin().get<RecentsViewStore> { parametersOf(viewModelScope) }
    }

    val favoritesState = favoritesStore.state
    val recentsState = recentsStore.state

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        checkLoginAndLoad()
    }

    private fun checkLoginAndLoad() {
        viewModelScope.launch {
            profileService.checkLoginStatus().fold(
                onSuccess = { loggedIn ->
                    _isLoggedIn.value = loggedIn
                    if (loggedIn) {
                        favoritesStore.onEvent(FavoritesEvent.LoadFavorites)
                        favoritesStore.checkNotificationPermission()
                    }
                },
                onError = {
                    _isLoggedIn.value = false
                }
            )
        }
    }

    fun onFavoritesEvent(event: FavoritesEvent) {
        favoritesStore.onEvent(event)
    }

    fun onRecentsEvent(event: RecentsEvent) {
        recentsStore.onEvent(event)
    }
}
