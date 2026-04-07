package org.example.fakeshop_clients.features.recents.di

import org.example.fakeshop_clients.features.recents.data.RecentsDatasource
import org.example.fakeshop_clients.features.recents.data.RecentsDatasourceImpl
import org.example.fakeshop_clients.features.recents.data.RecentsRepositoryImpl
import org.example.fakeshop_clients.features.recents.domain.RecentsRepository
import org.example.fakeshop_clients.features.recents.domain.RecentsService
import org.example.fakeshop_clients.features.recents.domain.RecentsServiceImpl
import org.koin.dsl.module

val recentsModule = module {

    factory<RecentsDatasource> {
        RecentsDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<RecentsRepository> {
        RecentsRepositoryImpl(
            datasource = get(),
            briefProductMapper = get()
        )
    }

    factory<RecentsService> {
        RecentsServiceImpl(
            repository = get()
        )
    }
}
