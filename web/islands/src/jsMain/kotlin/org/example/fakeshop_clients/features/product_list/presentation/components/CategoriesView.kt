package org.example.fakeshop_clients.features.product_list.presentation.components

import org.example.fakeshop_clients.features.product_list.presentation.CategoryRow
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

external interface CategoriesViewProps : Props {
    var categories: List<CategoryRow>
    var onProductClick: (String) -> Unit
}

val CategoriesView = FC<CategoriesViewProps> { props ->
    div {
        className = ClassName("categories-container")

        props.categories.forEach { categoryRow ->
            CategorySection {
                category = categoryRow
                onProductClick = props.onProductClick
            }
        }
    }
}