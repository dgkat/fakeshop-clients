package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.features.home.domain.GetProductsUseCase
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.ProductListServiceImpl
import org.koin.dsl.module

val homeModule = module {

    // Domain
    factory<GetProductsUseCase> {
        GetProductsUseCase()
    }

    factory<ProductListService> {
        ProductListServiceImpl(
            apiClient = get(),
            authRepository = get(),
        )
    }
}