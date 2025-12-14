package org.example.fakeshop_clients.features.search.di

import org.example.fakeshop_clients.features.search.domain.SearchService
import org.example.fakeshop_clients.features.search.domain.SearchServiceMock
import org.koin.dsl.module

val searchModule = module {
    factory<SearchService> {
        SearchServiceMock()
    }
}