package org.example.fakeshop_clients.features.productDetailPage.presentation

import FullProduct
import com.yourapp.eshop.server.templates.productDetailPage
import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import kotlinx.html.FlowContent
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.id
import kotlinx.html.span

fun Route.productRoutes() {

    // Product detail page
    get("/product/{id}") {
        val productId = call.parameters["id"] ?: return@get call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val product = ProductRepository.getProductById(productId)

        if (product == null) {
            call.respondText(
                "Product not found",
                status = HttpStatusCode.NotFound
            )
            return@get
        }

        call.respondHtml(HttpStatusCode.OK) {
            productDetailPage(product)
        }
    }

    // HTMX endpoint - Toggle like
    post("/api/products/{id}/like") {
        val productId = call.parameters["id"] ?: return@post call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val updatedProduct = ProductRepository.toggleLike(productId)

        if (updatedProduct == null) {
            call.respondText(
                "Product not found",
                status = HttpStatusCode.NotFound
            )
            return@post
        }

        // Return just the updated button HTML (HTMX will swap it)
        call.respondHtml(HttpStatusCode.OK) {
            body {
                likeButton(updatedProduct)
            }
        }
    }
}

fun FlowContent.likeButton(product: FullProduct) {
    button(classes = "btn btn-like ${if (product.isLiked) "liked" else ""}") {
        id = "like-button"

        // HTMX attributes
        attributes["hx-post"] = "/api/products/${product.id}/like"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-target"] = "#like-button"

        span(classes = "like-icon") {
            if (product.isLiked) +"❤️" else +"🤍"
        }
        span(classes = "like-text") {
            +if (product.isLiked) "Liked" else "Like"
        }
    }
}