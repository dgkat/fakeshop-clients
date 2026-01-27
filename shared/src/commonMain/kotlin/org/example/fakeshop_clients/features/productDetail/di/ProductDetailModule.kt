package org.example.fakeshop_clients.features.productDetail.di

import org.example.fakeshop_clients.features.productDetail.data.ProductDetailDatasource
import org.example.fakeshop_clients.features.productDetail.data.ProductDetailDatasourceImpl
import org.example.fakeshop_clients.features.productDetail.data.ProductDetailRepositoryImpl
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetail.domain.ProductDetailServiceImpl
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.domain.mappers.DomainToPresentationDetailedProductMapper
import org.koin.dsl.module

val productDetailModule = module {

    // Data
    factory {
        DataToDomainDetailedProductMapper()
    }

    factory<ProductDetailDatasource> {
        ProductDetailDatasourceImpl(
            authClient = get()
        )
    }

    // Domain
    factory<ProductDetailRepository> {
        ProductDetailRepositoryImpl(
            datasource = get(),
            briefProductMapper = get(), // Reuses home module's DataToDomainBriefProductMapper
            detailedProductMapper = get()
        )
    }

    factory<ProductDetailService> {
        ProductDetailServiceImpl(
            repository = get()
        )
    }

    // Presentation
    factory {
        DomainToPresentationBriefProductMapper()
    }

    factory {
        DomainToPresentationDetailedProductMapper()
    }
}
