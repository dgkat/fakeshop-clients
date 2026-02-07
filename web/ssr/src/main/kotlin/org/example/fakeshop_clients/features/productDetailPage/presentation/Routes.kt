package org.example.fakeshop_clients.features.productDetailPage.presentation

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
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.productDetailPage
import org.koin.ktor.ext.inject

fun Route.productRoutes() {
    val productDetailService by inject<ProductDetailService>()

    // Product detail page
    get("/product/{id}") {
        val productId = call.parameters["id"] ?: return@get call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = mutableMapOf<String, String>()
        call.request.cookies.rawCookies.forEach { (name, _) ->
            call.request.cookies[name]?.let { value ->
                cookies[name] = value
            }
        }

        val extractedCookies = Cookies(cookies)

        val fullProduct = productDetailService.getFullProductById(productId, extractedCookies)

        when (fullProduct) {
            is Result.Error -> {
                call.respondText(
                    "Failed to load product details: ${fullProduct.error}",
                    status = HttpStatusCode.InternalServerError
                )
            }

            is Result.Success -> {
                call.respondHtml(HttpStatusCode.OK) {
                    productDetailPage(fullProduct.data)
                }
            }
        }
    }

    // HTMX endpoint - Toggle like
    post("/product/like/{id}") {
        val productId = call.parameters["id"] ?: return@post call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = mutableMapOf<String, String>()
        call.request.cookies.rawCookies.forEach { (name, _) ->
            call.request.cookies[name]?.let { value ->
                cookies[name] = value
            }
        }

        val extractedCookies = Cookies(cookies)

        val toggleResult = productDetailService.toggleLike(productId, extractedCookies)

        when (toggleResult) {
            is Result.Error -> {
                call.respondText(
                    "Failed to toggle like: ${toggleResult.error}",
                    status = HttpStatusCode.InternalServerError
                )
            }

            is Result.Success -> {

                call.respondHtml(HttpStatusCode.OK) {
                    body {
                        likeButton(
                            productId = productId,
                            isLiked = true
                        )
                    }
                }
            }
        }
    }
}

fun FlowContent.likeButton(productId: String, isLiked: Boolean) {
    button(classes = "btn btn-like ${if (isLiked) "liked" else ""}") {
        id = "like-button"

        // HTMX attributes
        attributes["hx-post"] = "/product/like/$productId"
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-target"] = "#like-button"

        span(classes = "like-icon") {
            if (isLiked) +"❤️" else +"🤍"
        }
        span(classes = "like-text") {
            +if (isLiked) "Liked" else "Like"
        }
    }
}