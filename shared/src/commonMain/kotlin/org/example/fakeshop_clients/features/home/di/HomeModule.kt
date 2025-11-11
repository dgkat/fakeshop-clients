package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.core.auth.data.AuthDatasource
import org.example.fakeshop_clients.core.auth.data.AuthDatasourceImpl
import org.example.fakeshop_clients.features.home.domain.GetProductsUseCase
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.ProductListServiceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homeModule = module {

    // Domain
    factory<GetProductsUseCase> {
        GetProductsUseCase()
    }

    factory<AuthDatasource> {
        AuthDatasourceImpl(
            authClient = get(named("authClient"))
        )
    }
    factory<ProductListService> {
        ProductListServiceImpl(apiClient = get(), authDatasource = get())
    }
}