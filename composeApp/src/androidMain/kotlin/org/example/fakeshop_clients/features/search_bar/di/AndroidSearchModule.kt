package org.example.fakeshop_clients.features.search_bar.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.search.di.searchModule
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.example.fakeshop_clients.features.search_bar.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val androidSearchModule = module {

    includes(searchModule)

    factory<SearchViewStore> { (scope: CoroutineScope) ->
        SearchViewStore(scope = scope, searchService = get())
    }

    viewModel {
        SearchViewModel(storeFactory = { scope -> get { parametersOf(scope) } })
    }
}