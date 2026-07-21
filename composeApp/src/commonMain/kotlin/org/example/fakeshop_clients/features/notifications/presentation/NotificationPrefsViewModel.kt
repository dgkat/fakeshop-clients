package org.example.fakeshop_clients.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow

class NotificationPrefsViewModel(
    storeFactory: (CoroutineScope) -> NotificationPrefsViewStore
) : ViewModel() {

    private val store = storeFactory(viewModelScope)

    val state: StateFlow<NotificationPrefsState> = store.state

    fun onEvent(event: NotificationPrefsEvent) = store.onEvent(event)
}
