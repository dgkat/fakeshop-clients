package org.example.fakeshop_clients.features.product_list.presentation.components

import org.example.fakeshop_clients.features.home.presentation.productList.UiCategoryRow
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import web.cssom.ClassName

external interface CategoriesViewProps : Props {
    var categories: List<UiCategoryRow>
    var onProductClick: (productId: String, position: Int) -> Unit
    var favoritedProductIds: Set<String>
    var onToggleFavorite: ((String) -> Unit)?
}

val CategoriesView = FC<CategoriesViewProps> { props ->
    div {
        className = ClassName("categories-container")

        props.categories.forEach { categoryRow ->
            CategorySection {
                key = categoryRow.category
                category = categoryRow
                onProductClick = props.onProductClick
                favoritedProductIds = props.favoritedProductIds
                onToggleFavorite = props.onToggleFavorite
            }
        }
    }
}