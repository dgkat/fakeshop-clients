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
import org.example.fakeshop_clients.core.extensions.extractCookies
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.features.productDetailPage.domain.ProductDetailService
import org.example.fakeshop_clients.features.productDetailPage.presentation.pages.productDetailPage
import org.koin.ktor.ext.inject

fun Route.productRoutes() {
    val productDetailService by inject<ProductDetailService>()

    // Product detail page (locale-prefixed, under /{locale} group)
    get("/product/{id}") {
        val locale = call.parameters["locale"] ?: WebStrings.DEFAULT_LOCALE
        val strings = WebStrings.getAll(locale)
        val stringsJson = WebStrings.getAllAsJson(locale)
        val productId = call.parameters["id"] ?: return@get call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = call.extractCookies()

        val fullProduct = productDetailService.getFullProductById(productId, cookies)

        when (fullProduct) {
            is Result.Error -> {
                call.respondText(
                    "Unable to load product details. Please try again later.",
                    status = HttpStatusCode.InternalServerError
                )
            }

            is Result.Success -> {
                call.respondHtml(HttpStatusCode.OK) {
                    productDetailPage(fullProduct.data, locale, strings, stringsJson)
                }
            }
        }
    }
}

fun Route.productApiRoutes() {
    val productDetailService by inject<ProductDetailService>()

    // HTMX endpoint - Toggle like (no locale prefix)
    post("/product/like/{id}") {
        val productId = call.parameters["id"] ?: return@post call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = call.extractCookies()

        val toggleResult = productDetailService.toggleLike(productId, cookies)

        when (toggleResult) {
            is Result.Error -> {
                call.respondText(
                    "Unable to process request. Please try again later.",
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
