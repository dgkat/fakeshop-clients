package org.example.fakeshop_clients.features.profile.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class ProfileViewModel() : ViewModel() {
    private val store: ProfileViewStore by lazy {
        getKoin().get<ProfileViewStore> { parametersOf(viewModelScope) }
    }

    val uiState = store.profileState

    fun onEvent(event: ProfileEvent) {
        store.onEvent(event)
    }
}