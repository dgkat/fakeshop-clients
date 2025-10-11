package org.example.fakeshop_clients.features.home.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.example.fakeshop_clients.features.home.domain.GetProductsUseCase
import org.example.fakeshop_clients.features.home.presentation.HomeViewStore
import org.koin.dsl.module

val homeModule = module {

    // Domain
    factory<GetProductsUseCase>{
        GetProductsUseCase()
    }

    //Presentation
    //TODO get scope from actual VM
    factory<HomeViewStore> {
        HomeViewStore(
            getProductsUseCase = get(),
            scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        )
    }
}