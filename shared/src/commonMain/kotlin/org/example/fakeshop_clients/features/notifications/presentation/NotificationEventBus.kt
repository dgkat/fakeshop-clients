package org.example.fakeshop_clients.features.notifications.presentation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object NotificationEventBus {
    private val _events = MutableSharedFlow<PushNotificationEvent>()
    val events: SharedFlow<PushNotificationEvent> = _events.asSharedFlow()

    suspend fun emit(event: PushNotificationEvent) = _events.emit(event)
}

sealed class PushNotificationEvent {
    data class PriceDrop(
        val productId: String,
        val title: String,
        val body: String
    ) : PushNotificationEvent()

    data class OpenProduct(val productId: String) : PushNotificationEvent()
}
