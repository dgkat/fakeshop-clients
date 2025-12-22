package org.example.fakeshop_clients.features.search.di

import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webSearchModule = module {
    // Include shared searchModule for SearchService
    includes(searchModule)

    single {
        SearchViewStore(
            scope = get(qualifier = named("appScope")),
            searchService = get()
        )
    }

    factory { (store: SearchViewStore) ->
        SearchViewModel(store = store)
    }
}
