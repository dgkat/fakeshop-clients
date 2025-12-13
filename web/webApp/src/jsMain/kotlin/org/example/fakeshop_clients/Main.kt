package org.example.fakeshop_clients

import kotlinx.browser.document
import kotlinx.browser.window
import org.example.fakeshop_clients.core.di.webCoreModule
import org.example.fakeshop_clients.core.navigation.mobile.BottomNav
import org.example.fakeshop_clients.core.presentation.components.Header
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesPage
import org.example.fakeshop_clients.features.notifications.presentation.NotificationsPage
import org.example.fakeshop_clients.features.profile.presentation.ProfilePage
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import react.FC
import react.Props
import react.ReactElement
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.div
import react.router.Outlet
import react.router.RouteObject
import react.router.dom.RouterProvider
import react.router.dom.createBrowserRouter
import web.cssom.ClassName
import web.dom.Element

fun main() {
    startKoin {
        modules(webCoreModule)
    }

    val rootElement = document.getElementById("spa-root") as? Element
        ?: error("Root element not found")

    createRoot(rootElement).render(SpaApp.create())

    window.addEventListener("beforeunload", {
        stopKoin()
    })
}

private fun createRoute(
    path: String,
    element: ReactElement<*>,
    children: Array<RouteObject>? = null
): RouteObject = js("{}").unsafeCast<RouteObject>().apply {
    this.path = path
    this.element = element
    children?.let { this.children = it }
}

val SpaApp = FC<Props> {
    val router = createBrowserRouter(
        arrayOf(
            createRoute(
                path = "/",
                element = FC<Props> {
                    Header()

                    div {
                        className = ClassName("main-content")
                        div {
                            className = ClassName("container")
                            Outlet()
                        }
                    }
                    BottomNav()
                }.create(),
                children = arrayOf(
                    createRoute("favorites", FavoritesPage.create()),
                    createRoute("notifications", NotificationsPage.create()),
                    createRoute("profile", ProfilePage.create())
                )
            )
        )
    )

    RouterProvider {
        this.router = router
    }
}
