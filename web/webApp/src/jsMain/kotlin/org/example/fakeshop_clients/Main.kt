package org.example.fakeshop_clients

import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.core.di.webCoreModule
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.core.navigation.mobile.BottomNav
import org.example.fakeshop_clients.core.presentation.components.Header
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesPage
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.presentation.NotificationsPage
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.profile.presentation.ProfilePage
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.components.SearchBar
import org.example.fakeshop_clients.features.search.utils.BehaviorMapping
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.mp.KoinPlatform.getKoin
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
import react.router.useLocation
import react.router.useNavigate
import react.useMemo
import web.cssom.ClassName
import web.dom.Element

fun main() {
    startKoin {
        modules(webCoreModule)
    }

    registerServiceWorker()

    val rootElement = document.getElementById("spa-root") as? Element
        ?: error("Root element not found")

    createRoot(rootElement).render(SpaApp.create())

    window.addEventListener("load", { bootstrapSession() })

    // Re-initialize Koin when restored from bfcache (back-forward cache).
    // The beforeunload handler stops Koin, but if the browser caches the page
    // and the user navigates back, main() won't re-run — so we restart here.
    window.addEventListener("pageshow", { event ->
        if (event.asDynamic().persisted == true) {
            startKoin {
                modules(webCoreModule)
            }
        }
    })

    window.addEventListener("beforeunload", {
        stopKoin()
    })
}

/**
 * Registers the Firebase Cloud Messaging service worker on every page load.
 * This is cheap, idempotent, and ensures the browser can handle incoming
 * push notifications even if the Firebase SDK hasn't been lazily loaded yet.
 */
private fun registerServiceWorker() {
    val sw = window.navigator.asDynamic().serviceWorker ?: return
    sw.register("/firebase-messaging-sw.js")
}

/**
 * Seeds [SessionStore] from the backend on app load and, if the user is
 * logged in and has granted notification permission, re-registers the FCM
 * token so the backend always has a fresh token for this browser.
 */
private fun bootstrapSession() {
    val koin = getKoin()
    val appScope = koin.get<CoroutineScope>(AppScopeQualifier)
    appScope.launch {
        val profileService = koin.get<ProfileService>()
        val sessionMutator = koin.get<SessionMutator>()

        val isLoggedIn = (profileService.checkLoginStatus() as? Result.Success)?.data ?: false
        // TODO(step-5): replace with SessionBootstrapper.
        if (isLoggedIn) sessionMutator.setAuthenticated(Role.LOGGED_USER)
        else sessionMutator.setAuthenticated(Role.GUEST)

        val service = koin.get<NotificationsService>()
        if (isLoggedIn && service.getPermissionStatus() == NotificationPermissionStatus.GRANTED) {
            service.registerDeviceAfterAuth()
        }
    }
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
    val locale = I18n.locale

    val router = createBrowserRouter(
        arrayOf(
            createRoute(
                path = "/$locale",
                element = SpaLayout.create(),
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

val SpaLayout = FC<Props> {
    // Get SearchViewModel from Koin (singleton)
    val viewModel = useMemo { getKoin().get<SearchViewModel>() }
    val navigate = useNavigate()
    val location = useLocation()
    val locale = I18n.locale

    // Determine search bar behavior based on current route
    val searchBehavior = useMemo(location.pathname) {
        BehaviorMapping.getSearchBarBehaviorFromRoute(location.pathname)
    }

    // Determine header scroll behavior based on current route
    val headerBehavior = useMemo(location.pathname) {
        when {
            location.pathname.contains("/favorites") -> "scroll-reactive"
            location.pathname.contains("/profile") -> "static"
            location.pathname.contains("/notifications") -> "scroll-reactive"
            else -> "scroll-reactive"
        }
    }

    // Mobile: SearchBar rendered separately (outside header since header is hidden)
    div {
        className = ClassName("mobile-search-container")
        SearchBar {
            this.viewModel = viewModel
            this.behavior = searchBehavior
            this.onNavigateToProduct = { productId ->
                window.location.href = "/$locale/product/$productId"
            }
        }
    }

    // Desktop: Header contains SearchBar
    Header {
        this.behavior = headerBehavior
        this.searchViewModel = viewModel
        this.searchBehavior = searchBehavior
        this.onNavigateToProduct = { productId ->
            window.location.href = "/$locale/product/$productId"
        }
    }

    // Main content area
    div {
        className = ClassName("main-content")
        div {
            className = ClassName("container")
            Outlet()
        }
    }

    // Bottom navigation
    BottomNav()
}
