package org.example.fakeshop_clients.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.core.auth.di.mobileInfrastructureModule
import org.example.fakeshop_clients.core.di.iosInfrastructureModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.example.fakeshop_clients.features.search.di.searchModule
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val iosModule = module {
    factory { (scope: CoroutineScope) ->
        ProductListViewStore(
            scope = scope,
            productListService = get()
        )
    }

    factory { (scope: CoroutineScope)->
        SearchViewStore(
            scope = scope,
            searchService = get()
        )
    }
}

fun initKoinIos() = startKoin {
    modules(
        iosInfrastructureModule, mobileInfrastructureModule, homeModule, searchModule, iosModule
    )
}

class IOSKoinHelper : KoinComponent {
    fun getProductListViewStore(scope: CoroutineScope): ProductListViewStore {
        return get { parametersOf(scope) }
    }

    fun getSearchViewStore(scope: CoroutineScope): SearchViewStore {
        return get { parametersOf(scope) }
    }
}
