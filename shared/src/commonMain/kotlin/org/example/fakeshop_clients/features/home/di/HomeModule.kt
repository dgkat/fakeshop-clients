package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.features.home.data.ProductListDatasource
import org.example.fakeshop_clients.features.home.data.ProductListDatasourceImpl
import org.example.fakeshop_clients.features.home.data.ProductListRepositoryImpl
import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.home.domain.ProductListRepository
import org.example.fakeshop_clients.features.home.domain.ProductListService
import org.example.fakeshop_clients.features.home.domain.ProductListServiceImpl
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper
import org.koin.dsl.module

val homeModule = module {

    // Data
    factory {
        DataToDomainBriefProductMapper()
    }

    factory<ProductListDatasource> {
        ProductListDatasourceImpl(
            authClient = get()
        )
    }

    // Domain
    factory<ProductListRepository> {
        ProductListRepositoryImpl(
            datasource = get(),
            mapper = get()
        )
    }

    factory<ProductListService> {
        ProductListServiceImpl(
            repository = get()
        )
    }

    // Presentation

    factory {
        DomainToPresentationBriefProductMapper()
    }
}