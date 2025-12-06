package org.example.fakeshop_clients.features.homePage.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import org.example.fakeshop_clients.features.homePage.presentation.pages.homePage

fun Route.homeRoute() {
    get("/") {
        call.respondHtml(HttpStatusCode.OK) {
            homePage()
        }
    }
}