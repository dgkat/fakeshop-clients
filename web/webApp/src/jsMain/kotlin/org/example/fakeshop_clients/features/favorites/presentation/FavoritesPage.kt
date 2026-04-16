package org.example.fakeshop_clients.features.favorites.presentation

import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.core.i18n.getString
import org.example.fakeshop_clients.core.presentation.components.ProductCardWithHeart
import org.example.fakeshop_clients.core.presentation.models.UiBriefProduct
import org.example.fakeshop_clients.features.recents.presentation.RecentsEvent
import org.example.fakeshop_clients.features.recents.presentation.RecentsState
import org.koin.core.context.GlobalContext
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.useEffectWithCleanup
import react.useMemo
import react.useState
import web.cssom.ClassName

val FavoritesPage = FC<Props> {
    val koin = GlobalContext.get()
    val viewModel = useMemo { koin.get<FavoritesViewModel>() }

    var favoritesState by useState(FavoritesState())
    var recentsState by useState(RecentsState())
    var selectedTab by useState(0)

    useEffectWithCleanup(viewModel) {
        val scope = MainScope()

        viewModel.favoritesState
            .onEach { newState -> favoritesState = newState }
            .launchIn(scope)

        viewModel.recentsState
            .onEach { newState -> recentsState = newState }
            .launchIn(scope)

        onCleanup { scope.cancel() }
    }

    div {
        className = ClassName("favorites-page")

        when {
            favoritesState.error is FavoritesError.NotLoggedIn -> {
                LoginRequiredContent {}
            }
            favoritesState.isLoading && favoritesState.products.isEmpty() && favoritesState.error == null -> {
                div {
                    className = ClassName("favorites-loading")
                    span {
                        className = ClassName("spinner")
                    }
                }
            }
            else -> {
                if (favoritesState.showNotificationBanner) {
                    NotificationEnableBanner {
                        this.onEnableClick = {
                            viewModel.onFavoritesEvent(FavoritesEvent.RequestNotificationPermission)
                        }
                    }
                }

                // Tabs
                div {
                    className = ClassName("favorites-tabs")
                    button {
                        className = ClassName(if (selectedTab == 0) "tab-button active" else "tab-button")
                        onClick = { selectedTab = 0 }
                        +getString("tab_favorites")
                    }
                    button {
                        className = ClassName(if (selectedTab == 1) "tab-button active" else "tab-button")
                        onClick = {
                            selectedTab = 1
                            viewModel.onRecentsEvent(RecentsEvent.LoadRecents)
                        }
                        +getString("tab_recently_seen")
                    }
                }

                // Tab content
                if (selectedTab == 0) {
                    FavoritesTabContent {
                        this.state = favoritesState
                        this.onRemoveFavorite = { productId ->
                            viewModel.onFavoritesEvent(FavoritesEvent.RemoveFavorite(productId))
                        }
                        this.onRetry = {
                            viewModel.onFavoritesEvent(FavoritesEvent.Retry)
                        }
                    }
                } else {
                    RecentsTabContent {
                        this.state = recentsState
                        this.onRetry = {
                            viewModel.onRecentsEvent(RecentsEvent.Retry)
                        }
                    }
                }
            }
        }
    }
}

// MARK: - Login Required

val LoginRequiredContent = FC<Props> {
    val locale = I18n.locale

    div {
        className = ClassName("page-content page-placeholder")

        span {
            className = ClassName("placeholder-icon")
            +"\u2764\uFE0F"
        }
        p { +getString("login_required_message") }
        button {
            className = ClassName("btn-primary")
            onClick = {
                window.location.href = "/$locale/favorites/../profile"
            }
            +getString("go_to_profile")
        }
    }
}

// MARK: - Favorites Tab

external interface FavoritesTabProps : Props {
    var state: FavoritesState
    var onRemoveFavorite: (String) -> Unit
    var onRetry: () -> Unit
}

val FavoritesTabContent = FC<FavoritesTabProps> { props ->
    when {
        props.state.error != null -> {
            ErrorContent {
                this.onRetry = props.onRetry
            }
        }
        props.state.isLoading -> {
            div {
                className = ClassName("favorites-loading")
                span { className = ClassName("spinner") }
            }
        }
        props.state.products.isEmpty() -> {
            div {
                className = ClassName("page-content page-placeholder")
                span {
                    className = ClassName("placeholder-icon")
                    +"\u2764\uFE0F"
                }
                p { +getString("favorites_empty") }
            }
        }
        else -> {
            ProductGrid {
                this.products = props.state.products
                this.isFavorited = true
                this.onToggleFavorite = props.onRemoveFavorite
            }
        }
    }
}

// MARK: - Recents Tab

external interface RecentsTabProps : Props {
    var state: RecentsState
    var onRetry: () -> Unit
}

val RecentsTabContent = FC<RecentsTabProps> { props ->
    when {
        props.state.error != null -> {
            ErrorContent {
                this.onRetry = props.onRetry
            }
        }
        props.state.isLoading -> {
            div {
                className = ClassName("favorites-loading")
                span { className = ClassName("spinner") }
            }
        }
        props.state.products.isEmpty() -> {
            div {
                className = ClassName("page-content page-placeholder")
                span {
                    className = ClassName("placeholder-icon")
                    +"\uD83D\uDD70\uFE0F"
                }
                p { +getString("recents_empty") }
            }
        }
        else -> {
            ProductGrid {
                this.products = props.state.products
                this.isFavorited = false
                this.onToggleFavorite = null
            }
        }
    }
}

// MARK: - Product Grid

external interface ProductGridProps : Props {
    var products: List<UiBriefProduct>
    var isFavorited: Boolean
    var onToggleFavorite: ((String) -> Unit)?
}

val ProductGrid = FC<ProductGridProps> { props ->
    div {
        className = ClassName("product-grid")
        props.products.forEach { product ->
            ProductCardWithHeart {
                this.product = product
                this.isFavorited = props.isFavorited
                this.onToggleFavorite = props.onToggleFavorite?.let { callback -> { callback(product.id) } }
            }
        }
    }
}

// MARK: - Error Content

external interface ErrorContentProps : Props {
    var onRetry: () -> Unit
}

val ErrorContent = FC<ErrorContentProps> { props ->
    div {
        className = ClassName("page-content page-placeholder")
        p { +getString("error_network") }
        button {
            className = ClassName("btn-primary")
            onClick = { props.onRetry() }
            +getString("retry")
        }
    }
}

// MARK: - Notification Permission Banner

external interface NotificationEnableBannerProps : Props {
    var onEnableClick: () -> Unit
}

val NotificationEnableBanner = FC<NotificationEnableBannerProps> { props ->
    var isEnabling by useState(false)

    div {
        className = ClassName("notification-enable-banner")

        div {
            className = ClassName("notification-enable-banner-text")
            p {
                className = ClassName("notification-enable-banner-title")
                +getString("notification_permission_title")
            }
            p {
                className = ClassName("notification-enable-banner-message")
                +getString("notification_permission_message")
            }
        }

        button {
            className = ClassName("btn-primary")
            disabled = isEnabling
            onClick = {
                if (!isEnabling) {
                    isEnabling = true
                    props.onEnableClick()
                }
            }
            +if (isEnabling) getString("processing") else getString("notification_permission_enable")
        }
    }
}
