package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.features.notifications.WebNotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.WebPushTokenProvider
import org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsServiceImpl
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webNotificationsModule = module {

    includes(notificationsModule)

    single<PushTokenProvider> { WebPushTokenProvider() }

    single<NotificationsService> { get<NotificationsServiceImpl>() }

    single<NotificationPermissionManager> {
        WebNotificationPermissionManager()
    }

    factory {
        NotificationPrefsViewStore(
            scope = get(qualifier = named("appScope")),
            notificationsService = get()
        )
    }

    factory { NotificationPrefsViewModel(store = get()) }
}
