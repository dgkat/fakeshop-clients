package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.features.notifications.data.NotificationsDatasource
import org.example.fakeshop_clients.features.notifications.data.NotificationsDatasourceImpl
import org.example.fakeshop_clients.features.notifications.data.NotificationsRepositoryImpl
import org.example.fakeshop_clients.features.notifications.domain.NotificationsRepository
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsServiceImpl
import org.koin.dsl.module

val notificationsModule = module {

    factory<NotificationsDatasource> {
        NotificationsDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<NotificationsRepository> {
        NotificationsRepositoryImpl(
            datasource = get()
        )
    }

    single {
        NotificationsServiceImpl(
            repository = get(),
            pushTokenProvider = get(),
            permissionManager = get()
        )
    }
}
