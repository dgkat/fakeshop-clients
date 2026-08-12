package org.example.fakeshop_clients.features.home.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.home.presentation.ProductListViewModel
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.koin.core.module.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val androidHomeModule = module {

    includes(homeModule)

    factory<ProductListViewStore> { (scope: CoroutineScope) ->
        ProductListViewStore(
            scope = scope,
            productListService = get(),
            favoritesService = get(),
            mapper = get(),
            sessionObserver = get()
        )
    }

    viewModel {
        ProductListViewModel(storeFactory = { scope -> get { parametersOf(scope) } })
    }
}