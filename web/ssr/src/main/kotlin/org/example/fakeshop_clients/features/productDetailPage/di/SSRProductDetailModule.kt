package org.example.fakeshop_clients.features.productDetailPage.di

import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetailPage.data.ProductDetailDatasource
import org.example.fakeshop_clients.features.productDetailPage.data.SSRProductDetailDatasourceImpl
import org.example.fakeshop_clients.features.productDetailPage.data.SSRProductDetailRepositoryImpl
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailServiceImpl
import org.koin.core.qualifier.named
import org.koin.dsl.module

val ssrProductDetailModule = module {
    // Data
    single<ProductDetailDatasource> {
        SSRProductDetailDatasourceImpl(
            httpClient = get(named("publicHttpClient"))
        )
    }

    single {
        DataToDomainBriefProductMapper()
    }

    single {
        DataToDomainDetailedProductMapper()
    }

    //Domain
    single<ProductDetailRepository> {
        SSRProductDetailRepositoryImpl(
            productDetailDatasource = get(),
            dataToDomainBriefProductMapper = get(),
            dataToDomainDetailedProductMapper = get()
        )
    }

    single<ProductDetailService> {
        ProductDetailServiceImpl(
            productDetailRepository = get()
        )
    }

}