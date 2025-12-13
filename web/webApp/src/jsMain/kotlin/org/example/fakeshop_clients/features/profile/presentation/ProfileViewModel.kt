package org.example.fakeshop_clients.features.profile.presentation

class ProfileViewModel(
    private val store: ProfileViewStore
) {
    val uiState = store.profileState

    fun onEvent(event: ProfileEvent) {
        store.onEvent(event)
    }
}