package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.features.notifications.WebNotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.WebPushTokenProvider
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.domain.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webNotificationsModule = module {

    includes(notificationsModule)

    single<PushTokenProvider> {
        WebPushTokenProvider(vapidKey = "YOUR_VAPID_KEY_HERE")
    }

    single<NotificationPermissionManager> {
        WebNotificationPermissionManager()
    }

    factory {
        NotificationPrefsViewStore(
            scope = get(qualifier = named("appScope")),
            notificationsService = get()
        )
    }
}
