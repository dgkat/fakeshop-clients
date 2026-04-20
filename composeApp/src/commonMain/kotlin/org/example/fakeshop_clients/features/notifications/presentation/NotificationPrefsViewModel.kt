package org.example.fakeshop_clients.features.notifications.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.parameter.parametersOf
import org.koin.mp.KoinPlatform.getKoin

class NotificationPrefsViewModel : ViewModel() {

    private val store: NotificationPrefsViewStore by lazy {
        getKoin().get<NotificationPrefsViewStore> { parametersOf(viewModelScope) }
    }

    val state: StateFlow<NotificationPrefsState> = store.state

    fun onEvent(event: NotificationPrefsEvent) = store.onEvent(event)
}
