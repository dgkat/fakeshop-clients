package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.features.home.data.ProductListDatasource
import org.example.fakeshop_clients.features.home.data.ProductListDatasourceImpl
import org.example.fakeshop_clients.features.home.data.mappers.RemoteBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.ProductListServiceImpl
import org.koin.dsl.module

val homeModule = module {

    // Data
    factory<ProductListDatasource> {
        ProductListDatasourceImpl(
            authClient = get()
        )
    }

    factory {
        RemoteBriefProductMapper()
    }

    // Domain

    factory<ProductListService> {
        ProductListServiceImpl(
            datasource = get(),
            mapper = get()
        )
    }
}