package org.example.fakeshop_clients.features.spaPage.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.fakeshop_clients.features.spaPage.presentation.pages.spaPage

fun Route.spaRoutes() {
    get("/favorites") {
        call.respondHtml(HttpStatusCode.OK) { spaPage() }
    }

    get("/notifications") {
        call.respondHtml(HttpStatusCode.OK) { spaPage() }
    }

    get("/profile") {
        call.respondHtml(HttpStatusCode.OK) { spaPage() }
    }
}