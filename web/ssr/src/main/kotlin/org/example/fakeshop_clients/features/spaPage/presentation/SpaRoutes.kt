package org.example.fakeshop_clients.features.spaPage.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.fakeshop_clients.core.i18n.WebStrings
import org.example.fakeshop_clients.features.spaPage.presentation.pages.spaPage

fun Route.spaRoutes() {
    get("/favorites") {
        val locale = call.parameters["locale"] ?: WebStrings.DEFAULT_LOCALE
        val strings = WebStrings.getAll(locale)
        val stringsJson = WebStrings.getAllAsJson(locale)
        call.respondHtml(HttpStatusCode.OK) { spaPage(locale, strings, stringsJson) }
    }

    get("/notifications") {
        val locale = call.parameters["locale"] ?: WebStrings.DEFAULT_LOCALE
        val strings = WebStrings.getAll(locale)
        val stringsJson = WebStrings.getAllAsJson(locale)
        call.respondHtml(HttpStatusCode.OK) { spaPage(locale, strings, stringsJson) }
    }

    get("/profile") {
        val locale = call.parameters["locale"] ?: WebStrings.DEFAULT_LOCALE
        val strings = WebStrings.getAll(locale)
        val stringsJson = WebStrings.getAllAsJson(locale)
        call.respondHtml(HttpStatusCode.OK) { spaPage(locale, strings, stringsJson) }
    }
}
