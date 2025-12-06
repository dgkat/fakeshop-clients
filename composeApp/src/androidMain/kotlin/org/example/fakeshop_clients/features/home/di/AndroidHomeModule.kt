package org.example.fakeshop_clients.features.home.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.home.presentation.ProductListViewModel
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.example.fakeshop_clients.features.search_bar.presentation.SearchViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidHomeModule = module {

    factory<ProductListViewStore> { (scope: CoroutineScope) ->
        ProductListViewStore(scope = scope, productListService = get())
    }

    viewModel {
        ProductListViewModel()
    }

    viewModel {
        SearchViewModel(
            /*searchViewStore = SearchViewStore(
                scope = it.get() // ViewModel's scope will be passed
            )*/
        )
    }
}