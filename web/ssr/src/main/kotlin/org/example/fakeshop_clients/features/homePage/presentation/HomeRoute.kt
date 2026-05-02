package org.example.fakeshop_clients.features.homePage.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.fakeshop_clients.core.auth.SSRGuestDatasource
import org.example.fakeshop_clients.core.extensions.ensureGuestSession
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.core.pages.bootstrapFailedPage
import org.example.fakeshop_clients.features.homePage.presentation.pages.homePage
import org.koin.ktor.ext.inject

fun Route.homeRoute() {
    val ssrGuestDatasource by inject<SSRGuestDatasource>()

    get("/") {
        val locale = call.parameters["locale"] ?: WebStrings.DEFAULT_LOCALE
        val strings = WebStrings.getAll(locale)
        val stringsJson = WebStrings.getAllAsJson(locale)

        if (call.ensureGuestSession(ssrGuestDatasource) == null) {
            call.respondHtml(HttpStatusCode.ServiceUnavailable) {
                bootstrapFailedPage(locale, strings)
            }
            return@get
        }

        call.respondHtml(HttpStatusCode.OK) {
            homePage(locale, strings, stringsJson)
        }
    }
}
