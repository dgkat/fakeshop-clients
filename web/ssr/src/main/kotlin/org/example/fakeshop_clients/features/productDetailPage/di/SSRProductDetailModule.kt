package org.example.fakeshop_clients.features.productDetailPage.di

import org.example.fakeshop_clients.features.home.data.mappers.DataToDomainBriefProductMapper
import org.example.fakeshop_clients.features.productDetail.data.mappers.DataToDomainDetailedProductMapper
import org.example.fakeshop_clients.features.productDetailPage.data.ProductDetailDatasource
import org.example.fakeshop_clients.features.productDetailPage.data.SSRProductDetailDatasourceImpl
import org.example.fakeshop_clients.features.productDetailPage.data.SSRProductDetailRepositoryImpl
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailRepository
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailServiceImpl
import org.koin.dsl.module

val ssrProductDetailModule = module {
    single<ProductDetailDatasource> {
        SSRProductDetailDatasourceImpl(
            safeApiClient = get(),
            baseUrl = get()
        )
    }

    single { DataToDomainBriefProductMapper() }
    single { DataToDomainDetailedProductMapper() }

    single<ProductDetailRepository> {
        SSRProductDetailRepositoryImpl(
            productDetailDatasource = get(),
            dataToDomainBriefProductMapper = get(),
            dataToDomainDetailedProductMapper = get()
        )
    }

    single<ProductDetailService> {
        ProductDetailServiceImpl(
            productDetailRepository = get(),
            bduiTemplateDatasource = get(),
            bduiTemplateMapper = get()
        )
    }
}
