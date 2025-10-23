package org.example.fakeshop_clients.island.presentation.components

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.island.presentation.ProductListState
import org.example.fakeshop_clients.island.presentation.ProductListViewmodel
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.p
import react.useEffect
import react.useState

external interface ProductListProps : Props {
    var viewModel: ProductListViewmodel?
}

val ProductListView = FC<ProductListProps> { props ->
    var state by useState(ProductListState(isLoading = true))

    useEffect(props.viewModel) {
        if (props.viewModel != null) {
            val scope = MainScope()
            val job = props.viewModel!!.uiState
                .onEach { newState ->
                    state = newState
                }
                .launchIn(scope)

            job::cancel
        } else {
            null
        }
    }
    //TODO find difference between and make sure job::cancel works
 /*   useEffect(props.viewModel) {
        val scope = MainScope()

        props.viewModel?.let {
            it.loadCategories(scope)
            val job = it.uiState
                .onEach { newState ->
                    state = newState
                }
                .launchIn(scope)

            job.cancel()
        }
    }*/

    div {
        when {
            state.isLoading -> {
                p { +"Loading products..." }
                button {
                    onClick = {
                        console.log("[Get fake items] Clicked!")
                        props.viewModel?.loadCategories()
                    }
                    +"Get fake items "
                }
            }

            state.error != null -> {
                p { +"Error: ${state.error}" }
            }

            else -> {
                state.categories.forEach { categoryRow ->
                    div {

                        h3 { +categoryRow.category }
                        div {
                            categoryRow.products.forEach { product ->
                                div {

                                    p { +product.name }
                                    p { +"$${product.price}" }
                                    button {
                                        +"View Details"
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}