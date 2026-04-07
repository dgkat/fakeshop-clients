package org.example.fakeshop_clients.features.recents.presentation

sealed class RecentsEvent {
    data object LoadRecents : RecentsEvent()
    data object Retry : RecentsEvent()
}
