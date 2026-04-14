package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.core.notifications.AndroidNotificationPermissionManager
import org.example.fakeshop_clients.core.notifications.AndroidPendingDeviceTokenCache
import org.example.fakeshop_clients.core.notifications.AndroidPushTokenProvider
import org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.data.PendingDeviceTokenCache
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidNotificationsModule = module {

    includes(notificationsModule)

    single<PushTokenProvider> { AndroidPushTokenProvider() }

    single<PendingDeviceTokenCache> {
        AndroidPendingDeviceTokenCache(androidContext())
    }

    single<NotificationPermissionManager> {
        AndroidNotificationPermissionManager(androidContext())
    }

    factory { (scope: kotlinx.coroutines.CoroutineScope) ->
        NotificationPrefsViewStore(
            scope = scope,
            notificationsService = get()
        )
    }

    viewModel { NotificationPrefsViewModel() }
}
