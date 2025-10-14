package org.example.fakeshop_clients.core.presentation.components

import org.example.fakeshop_clients.features.home.presentation.models.UiBriefProduct
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.dom.html.ReactHTML.span
import web.cssom.ClassName

external interface ProductCardProps : Props {
    var product: UiBriefProduct
    var onClick: (String) -> Unit
}

val ProductCard = FC<ProductCardProps> { props ->
    div {
        className = ClassName("product-card")
        onClick = {
            props.onClick(props.product.id)
            kotlinx.browser.window.location.href =
                "http://localhost:8081/product/${props.product.id}"
        }

        // Product Image
        div {
            className = ClassName("product-image-container")
            img {
                src = props.product.imageUrl
                alt = props.product.name
                className = ClassName("product-image")
            }
        }

        // Product Info
        div {
            className = ClassName("product-info")

            // Category Badge
            span {
                className = ClassName("product-category")
                +props.product.category
            }

            // Product Name
            div {
                className = ClassName("product-name")
                +props.product.name
            }

            // Price
            div {
                className = ClassName("product-price")
                +"$${props.product.price.asDynamic().toFixed(2)}"
            }
        }
    }
}