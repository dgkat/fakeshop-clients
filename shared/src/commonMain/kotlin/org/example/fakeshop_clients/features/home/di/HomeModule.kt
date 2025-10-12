package org.example.fakeshop_clients.features.home.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.home.domain.GetProductsUseCase
import org.example.fakeshop_clients.features.home.presentation.HomeViewStore
import org.koin.dsl.module

val homeModule = module {

    // Domain
    factory<GetProductsUseCase> {
        GetProductsUseCase()
    }

    //Presentation
    factory<HomeViewStore> { (scope: CoroutineScope) ->
        HomeViewStore(getProductsUseCase = get(), scope = scope)
    }
}