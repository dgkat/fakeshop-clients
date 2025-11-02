package org.example.fakeshop_clients.features.home.presentation

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.core.presentation.components.ProductCard
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.p
import react.useEffect
import react.useState
import web.cssom.ClassName

//TODO inject vm ?
external interface HomeScreenWebProps : Props {
    var viewModel: HomeViewmodel
}

val HomeScreenWeb = FC<HomeScreenWebProps> { props ->
    var uiState by useState<HomeState>(HomeState.Loading)


    useEffect(props.viewModel) {
        val scope = MainScope()
        val job = props.viewModel.uiState
            .onEach { newState ->
                uiState = newState
            }
            .launchIn(scope)

        job::cancel
    }

    div {
        className = ClassName("app-container")

        // Top Bar
        div {
            className = ClassName("top-bar")
            div {
                className = ClassName("top-bar-content")
                h1 {
                    className = ClassName("top-bar-title")
                    +"E-Shop"
                }
            }
        }

        // Main Content
        div {
            className = ClassName("main-content")
            when (val state = uiState) {
                is HomeState.Loading -> {
                    LoadingState()
                }

                is HomeState.Success -> {
                    ProductGrid {
                        products = state.products
                        onProductClick = props.viewModel::onProductClick
                    }
                }

                is HomeState.Error -> {
                    ErrorState {
                        message = state.message
                        onRetry = props.viewModel::loadProducts
                    }
                }
            }
        }
    }
}

private val LoadingState = FC<Props> {
    div {
        className = ClassName("loading-container")
        div {
            className = ClassName("spinner")
        }
        p {
            className = ClassName("loading-text")
            +"Loading products..."
        }
    }
}

external interface ErrorStateProps : Props {
    var message: String
    var onRetry: () -> Unit
}

private val ErrorState = FC<ErrorStateProps> { props ->
    div {
        className = ClassName("error-container")
        div {
            className = ClassName("error-emoji")
            +"😔"
        }
        div {
            className = ClassName("error-title")
            +"Oops! Something went wrong"
        }
        p {
            className = ClassName("error-message")
            +props.message
        }
        button {
            className = ClassName("retry-button")
            onClick = { props.onRetry() }
            +"Retry"
        }
    }
}

// Product Grid Component
external interface ProductGridProps : Props {
    var products: List<UiBriefProduct>
    var onProductClick: (String) -> Unit
}

private val ProductGrid = FC<ProductGridProps> { props ->
    div {
        className = ClassName("product-grid")
        props.products.forEach { product ->
            ProductCard {
                this.product = product
                this.onClick = props.onProductClick
            }
        }
    }
}