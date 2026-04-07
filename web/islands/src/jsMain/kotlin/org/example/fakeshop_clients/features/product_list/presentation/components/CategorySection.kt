package org.example.fakeshop_clients.features.product_list.presentation.components

import org.example.fakeshop_clients.features.home.presentation.productList.UiCategoryRow
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.useEffect
import react.useRef
import react.useState
import web.cssom.ClassName

external interface CategorySectionProps : Props {
    var category: UiCategoryRow
    var onProductClick: (String) -> Unit
    var favoritedProductIds: Set<String>
    var onToggleFavorite: ((String) -> Unit)?
}

val CategorySection = FC<CategorySectionProps> { props ->
    val scrollRef = useRef<web.html.HTMLDivElement>(null)
    var showLeftShadow by useState(false)
    var showRightShadow by useState(true)

    // Check scroll position
    val checkScroll = {
        scrollRef.current?.let { element ->
            val scrollLeft = element.scrollLeft
            val scrollWidth = element.scrollWidth
            val clientWidth = element.clientWidth

            // Show left shadow if scrolled from start
            showLeftShadow = scrollLeft > 10

            // Show right shadow if not at end
            showRightShadow = scrollLeft < (scrollWidth - clientWidth - 10)
        }
    }

    // Check scroll on mount and when products change
    useEffect(props.category.products) {
        checkScroll()
    }

    div {
        className = ClassName("category-section")

        // Category header
        h3 {
            className = ClassName("category-title")
            +props.category.category
        }

        // Wrapper for products row with shadows
        div {
            className = when {
                showLeftShadow && showRightShadow -> ClassName("products-row-wrapper show-left-shadow show-right-shadow")
                showLeftShadow -> ClassName("products-row-wrapper show-left-shadow")
                showRightShadow -> ClassName("products-row-wrapper show-right-shadow")
                else -> ClassName("products-row-wrapper")
            }

            // Products row (horizontal scroll)
            div {
                ref = scrollRef
                className = ClassName("products-row")
                onScroll = {
                    checkScroll()
                }

                props.category.products.forEach { product ->
                    ProductCard {
                        key = product.id
                        this.product = product
                        onClick = props.onProductClick
                        isFavorited = product.id in props.favoritedProductIds
                        onToggleFavorite = props.onToggleFavorite?.let { toggle ->
                            { toggle(product.id) }
                        }
                    }
                }
            }
        }
    }
}