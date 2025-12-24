package org.example.fakeshop_clients.features.search.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.dsl.module

val webSearchModule = module {
    includes(searchModule)

    factory { (scope: CoroutineScope) ->
        SearchViewStore(
            scope = scope,
            searchService = get()
        )
    }

    factory { (store: SearchViewStore) ->
        SearchViewModel(store = store)
    }
}
