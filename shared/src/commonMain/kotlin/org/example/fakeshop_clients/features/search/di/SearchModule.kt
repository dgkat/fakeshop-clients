package org.example.fakeshop_clients.features.search.di

import org.example.fakeshop_clients.features.search.data.SearchDatasource
import org.example.fakeshop_clients.features.search.data.SearchDatasourceImpl
import org.example.fakeshop_clients.features.search.data.SearchRepositoryImpl
import org.example.fakeshop_clients.features.search.domain.SearchRepository
import org.example.fakeshop_clients.features.search.domain.SearchService
import org.example.fakeshop_clients.features.search.data.SearchServiceImpl
import org.koin.dsl.module

val searchModule = module {
    factory<SearchDatasource> {
        SearchDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<SearchRepository> {
        SearchRepositoryImpl(datasource = get())
    }

    factory<SearchService> {
        SearchServiceImpl(repository = get())
    }
}