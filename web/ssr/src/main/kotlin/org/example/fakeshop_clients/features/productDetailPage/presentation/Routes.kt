package org.example.fakeshop_clients.features.productDetailPage.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.delete
import io.ktor.server.routing.post
import kotlinx.html.FlowContent
import kotlinx.html.body
import kotlinx.html.button
import kotlinx.html.id
import org.example.fakeshop_clients.core.design.IconPaths
import org.example.fakeshop_clients.core.ui.svgIcon
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

    // HTMX endpoint - Check initial like state on page load (intersect once)
    get("/product/like/{id}") {
        val productId = call.parameters["id"] ?: return@get call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = call.extractCookies()
        val isLiked = when (val result = productDetailService.checkFavorite(productId, cookies)) {
            is Result.Success -> result.data
            is Result.Error -> false
        }

        call.respondHtml(HttpStatusCode.OK) {
            body {
                likeButton(productId = productId, isLiked = isLiked)
            }
        }
    }

    // HTMX endpoint - Add like
    post("/product/like/{id}") {
        val productId = call.parameters["id"] ?: return@post call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = call.extractCookies()

        when (productDetailService.addFavorite(productId, cookies)) {
            is Result.Error -> call.respondText(
                "Unable to process request. Please try again later.",
                status = HttpStatusCode.InternalServerError
            )
            is Result.Success -> call.respondHtml(HttpStatusCode.OK) {
                body { likeButton(productId = productId, isLiked = true) }
            }
        }
    }

    // HTMX endpoint - Remove like
    delete("/product/like/{id}") {
        val productId = call.parameters["id"] ?: return@delete call.respondText(
            "Product ID is required",
            status = HttpStatusCode.BadRequest
        )

        val cookies = call.extractCookies()

        when (productDetailService.removeFavorite(productId, cookies)) {
            is Result.Error -> call.respondText(
                "Unable to process request. Please try again later.",
                status = HttpStatusCode.InternalServerError
            )
            is Result.Success -> call.respondHtml(HttpStatusCode.OK) {
                body { likeButton(productId = productId, isLiked = false) }
            }
        }
    }
}

fun FlowContent.likeButton(productId: String, isLiked: Boolean) {
    button(classes = "btn-like-circle ${if (isLiked) "liked" else ""}") {
        id = "like-button"

        if (isLiked) {
            attributes["hx-delete"] = "/product/like/$productId"
        } else {
            attributes["hx-post"] = "/product/like/$productId"
        }
        attributes["hx-swap"] = "outerHTML"
        attributes["hx-target"] = "#like-button"

        svgIcon(pathData = IconPaths.HEART, cssClass = "like-icon")
    }
}
