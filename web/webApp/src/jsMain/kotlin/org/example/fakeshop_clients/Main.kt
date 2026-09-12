package org.example.fakeshop_clients

import org.example.fakeshop_clients.core.interactions.domain.InteractionQuery
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.core.di.webCoreModule
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.core.navigation.mobile.BottomNav
import org.example.fakeshop_clients.core.presentation.BootstrapFailedPage
import org.example.fakeshop_clients.core.presentation.components.Header
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesPage
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.presentation.NotificationsPage
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
import react.useEffectWithCleanup
import react.useMemo
import react.useState
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

    // When restored from bfcache, force a fresh load so Koin and React start
    // clean. Partial revival (restarting Koin alone) leaves React components
    // holding stale bean references and skips bootstrapSession() entirely.
    window.addEventListener("pageshow", { event ->
        if (event.asDynamic().persisted == true) {
            window.location.reload()
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

private fun bootstrapSession() {
    val koin = getKoin()
    val appScope = koin.get<CoroutineScope>(AppScopeQualifier)
    appScope.launch {
        koin.get<SessionBootstrapper>().bootstrap()

        val state = koin.get<SessionObserver>().state.value
        if (state is SessionState.Authenticated && state.role != Role.GUEST) {
            val service = koin.get<NotificationsService>()
            if (service.getPermissionStatus() == NotificationPermissionStatus.GRANTED) {
                service.registerDeviceAfterAuth()
            }
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
    val koin = useMemo { getKoin() }
    val sessionObserver = useMemo { koin.get<SessionObserver>() }

    var sessionReady by useState { sessionObserver.state.value is SessionState.Authenticated }
    var bootstrapFailed by useState { sessionObserver.state.value is SessionState.BootstrapFailed }

    useEffectWithCleanup(sessionObserver) {
        val scope = MainScope()
        sessionObserver.state
            .onEach { state ->
                sessionReady = state is SessionState.Authenticated
                bootstrapFailed = state is SessionState.BootstrapFailed
            }
            .launchIn(scope)
        onCleanup { scope.cancel() }
    }

    val router = useMemo(locale) {
        createBrowserRouter(
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
    }

    when {
        bootstrapFailed -> BootstrapFailedPage { }
        sessionReady -> RouterProvider { this.router = router }
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
            this.onNavigateToProduct = { productId, position ->
                window.location.href = InteractionQuery.productDetailPath(
                    locale = locale,
                    productId = productId,
                    surface = InteractionSurface.SEARCH,
                    position = position
                )
            }
        }
    }

    // Desktop: Header contains SearchBar
    Header {
        this.behavior = headerBehavior
        this.searchViewModel = viewModel
        this.searchBehavior = searchBehavior
        this.onNavigateToProduct = { productId, position ->
            window.location.href = InteractionQuery.productDetailPath(
                locale = locale,
                productId = productId,
                surface = InteractionSurface.SEARCH,
                position = position
            )
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
