package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.core.notifications.AndroidNotificationPermissionManager
import org.example.fakeshop_clients.core.notifications.AndroidNotificationsService
import org.example.fakeshop_clients.core.notifications.AndroidPendingDeviceTokenCache
import org.example.fakeshop_clients.core.notifications.AndroidPushTokenProvider
import org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsServiceImpl
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val androidNotificationsModule = module {

    includes(notificationsModule)

    single<PushTokenProvider> { AndroidPushTokenProvider() }

    single { AndroidPendingDeviceTokenCache(androidContext()) }

    single<NotificationPermissionManager> {
        AndroidNotificationPermissionManager(androidContext())
    }

    single<NotificationsService> {
        AndroidNotificationsService(
            delegate = get<NotificationsServiceImpl>(),
            pendingDeviceTokenCache = get()
        )
    }

    factory { (scope: kotlinx.coroutines.CoroutineScope) ->
        NotificationPrefsViewStore(
            scope = scope,
            notificationsService = get(),
            sessionObserver = get()
        )
    }

    viewModel {
        NotificationPrefsViewModel(storeFactory = { scope -> get { parametersOf(scope) } })
    }
}
