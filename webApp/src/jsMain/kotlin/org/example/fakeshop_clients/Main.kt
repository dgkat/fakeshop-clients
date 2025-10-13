package org.example.fakeshop_clients

import kotlinx.browser.document
import kotlinx.browser.window
import org.example.fakeshop_clients.core.di.webCoreModule
import org.example.fakeshop_clients.features.home.presentation.HomeScreenWeb
import org.example.fakeshop_clients.features.home.presentation.HomeViewmodel
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import react.create
import react.dom.client.createRoot
import web.dom.Element

fun main() {
    startKoin {
        modules(webCoreModule)
    }

    val koin = GlobalContext.get()
    val viewModel = koin.get<HomeViewmodel>()

    val rootElement = document.getElementById("root") as? Element
        ?: error("Root element not found")

    createRoot(rootElement).render(
        HomeScreenWeb.create {
            this.viewModel = viewModel
        }
    )

    window.addEventListener("beforeunload", {
        stopKoin()
    })
}