package org.example.fakeshop_clients.features.search.di

import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webSearchModule = module {
    includes(searchModule)

    single {
        SearchViewStore(
            scope = get(qualifier = named("appScope")),
            searchService = get()
        )
    }

    factory {
        SearchViewModel(store = get())
    }
}
