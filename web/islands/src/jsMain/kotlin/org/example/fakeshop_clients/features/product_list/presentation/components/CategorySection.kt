package org.example.fakeshop_clients.features.product_list.presentation.components

import org.example.fakeshop_clients.features.product_list.presentation.CategoryRow
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import web.cssom.ClassName

external interface CategorySectionProps : Props {
    var category: CategoryRow
    var onProductClick: (String) -> Unit
}

val CategorySection = FC<CategorySectionProps> { props ->
    div {
        className = ClassName("category-section")

        // Category header
        h3 {
            className = ClassName("category-title")
            +props.category.category
        }

        // Products row (horizontal scroll)
        div {
            className = ClassName("products-row")

            props.category.products.forEach { product ->
                ProductCard {
                    this.product = product
                    onClick = props.onProductClick
                }
            }
        }
    }
}