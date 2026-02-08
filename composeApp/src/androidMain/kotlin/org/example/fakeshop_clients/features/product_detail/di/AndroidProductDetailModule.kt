package org.example.fakeshop_clients.features.product_detail.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.productDetail.di.productDetailModule
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailViewStore
import org.example.fakeshop_clients.features.product_detail.presentation.ProductDetailViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidProductDetailModule = module {

    includes(productDetailModule)

    factory<ProductDetailViewStore> { (scope: CoroutineScope) ->
        ProductDetailViewStore(
            scope = scope,
            productDetailService = get(),
            briefProductMapper = get(),
            detailedProductMapper = get()
        )
    }

    viewModel {
        ProductDetailViewModel()
    }
}
