package org.example.fakeshop_clients.features.product_list.presentation.components

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.features.product_list.presentation.ProductListState
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
        if (props.viewModel != null) {
            val scope = MainScope()
            val job = props.viewModel!!.uiState
                .onEach { newState ->
                    state = newState
                }
                .launchIn(scope)

            onCleanup { job::cancel }
        } else {
            null
        }
    }

    div {
        className = ClassName("product-list-container")

        when {
            state.isLoading -> {
                LoadingView {
                    onLoadClick = { props.viewModel?.loadCategories() }
                }
            }

            state.error != null -> {
                ErrorView {
                    errorMessage = state.error!!
                }
            }

            else -> {
                CategoriesView {
                    categories = state.categories
                    onProductClick = { productId ->
                        console.log("[ProductList] Product clicked: $productId")
                    }
                }
            }
        }
    }
}