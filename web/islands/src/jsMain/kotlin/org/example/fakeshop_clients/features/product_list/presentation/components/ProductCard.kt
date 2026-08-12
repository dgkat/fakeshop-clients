package org.example.fakeshop_clients.features.product_list.presentation.components

import org.example.fakeshop_clients.core.design.IconPaths
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.svg.ReactSVG.path
import react.dom.svg.ReactSVG.svg
import web.cssom.ClassName

external interface ProductCardProps : Props {
    var product: UiBriefProduct
    var onClick: (String) -> Unit
    var isFavorited: Boolean
    var onToggleFavorite: (() -> Unit)?
}

val ProductCard = FC<ProductCardProps> { props ->
    div {
        className = ClassName("product-card")
        onClick = {
            props.onClick(props.product.id)
        }

        div {
            className = ClassName("product-image-container")
            img {
                src = props.product.imageUrl
                alt = props.product.name
                className = ClassName("product-image")
            }

            props.onToggleFavorite?.let { toggle ->
                button {
                    className = ClassName(if (props.isFavorited) "btn-like-circle liked" else "btn-like-circle")
                    onClick = { e ->
                        e.stopPropagation()
                        toggle()
                    }
                    svg {
                        className = ClassName("like-icon")
                        viewBox = "0 0 24 24"
                        path {
                            d = IconPaths.HEART
                        }
                    }
                }
            }
        }

        div {
            className = ClassName("product-info")

            div {
                className = ClassName("product-name")
                +props.product.name
            }

            div {
                className = ClassName("product-price")
                +props.product.formattedPrice
            }
        }
    }
}
