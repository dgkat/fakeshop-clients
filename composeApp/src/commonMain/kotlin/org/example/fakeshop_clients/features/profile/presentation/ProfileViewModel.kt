package org.example.fakeshop_clients.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope

class ProfileViewModel(
    storeFactory: (CoroutineScope) -> ProfileViewStore
) : ViewModel() {
    private val store = storeFactory(viewModelScope)

    val uiState = store.profileState

    fun onEvent(event: ProfileEvent) {
        store.onEvent(event)
    }
}
