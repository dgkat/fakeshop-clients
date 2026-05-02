package org.example.fakeshop_clients.features.product_list.di

import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.example.fakeshop_clients.features.product_list.presentation.ProductListViewmodel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val productListModule = module {

    factory {
        ProductListViewStore(
            scope = get(qualifier = named("appScope")),
            productListService = get(),
            favoritesService = get(),
            mapper = get(),
            sessionObserver = get()
        )
    }

    factory { ProductListViewmodel(store = get()) }
}