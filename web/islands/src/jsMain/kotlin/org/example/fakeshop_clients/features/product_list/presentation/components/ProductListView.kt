package org.example.fakeshop_clients.features.product_list.presentation.components

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListState
import org.example.fakeshop_clients.features.product_list.presentation.ProductListViewmodel
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useEffectWithCleanup
import react.useState
import web.cssom.ClassName

external interface ProductListProps : Props {
    var viewModel: ProductListViewmodel?
}

val ProductListView = FC<ProductListProps> { props ->
    var state by useState(ProductListState(isLoading = true))

    useEffectWithCleanup(props.viewModel) {
        val vm = props.viewModel
        if (vm != null) {
            val scope = MainScope()
            vm.uiState
                .onEach { newState -> state = newState }
                .launchIn(scope)

            onCleanup { scope.cancel() }
        } else {
            null
        }
    }

    div {
        className = ClassName("product-list-container")

        when {
            state.isLoading -> {
                LoadingView {
                    onLoadClick = { }
                }
            }

            state.error != null -> {
                ErrorView {
                    errorMessage = state.error.toString()
                }
            }

            else -> {
                CategoriesView {
                    categories = state.categories
                    onProductClick = { productId ->
                        val locale = I18n.locale
                        window.location.href = "/$locale/product/$productId"
                    }
                    favoritedProductIds = state.favoritedProductIds
                    onToggleFavorite = props.viewModel?.let { vm ->
                        { productId: String -> vm.toggleFavorite(productId) }
                    }
                }
            }
        }
    }
}