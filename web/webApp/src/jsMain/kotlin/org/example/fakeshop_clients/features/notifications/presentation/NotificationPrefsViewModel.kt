package org.example.fakeshop_clients.features.notifications.presentation

class NotificationPrefsViewModel(
    private val store: NotificationPrefsViewStore
) {
    val state = store.state

    fun onEvent(event: NotificationPrefsEvent) {
        store.onEvent(event)
    }
}
