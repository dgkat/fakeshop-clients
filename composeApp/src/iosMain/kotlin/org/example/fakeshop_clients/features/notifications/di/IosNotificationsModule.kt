package org.example.fakeshop_clients.features.notifications.di

import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val iosNotificationsModule = module {
    viewModel {
        NotificationPrefsViewModel(storeFactory = { scope -> get { parametersOf(scope) } })
    }
}
