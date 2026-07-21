package org.example.fakeshop_clients.features.favorites.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService

class FavoritesViewStore(
    private val scope: CoroutineScope,
    private val favoritesService: FavoritesService,
    private val mapper: DomainToPresentationBriefProductMapper,
    private val notificationsService: NotificationsService,
    private val sessionObserver: SessionObserver
) {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    private var loadJob: Job? = null

    init {
        scope.launch {
            favoritesService.favoritedIds.collect { ids ->
                _state.update { current ->
                    if (current.products.isNotEmpty()) {
                        current.copy(products = current.products.filter { it.id in ids })
                    } else {
                        current
                    }
                }
            }
        }
        sessionObserver.upgradeInProgress
            .combine(sessionObserver.state.map { it is SessionState.Unknown }) { upgrading, unknown ->
                upgrading || unknown
            }
            .onEach { blocked -> _state.update { it.copy(writesBlocked = blocked) } }
            .launchIn(scope)
        loadFavorites()
    }

    fun onEvent(event: FavoritesEvent) {
        when (event) {
            FavoritesEvent.LoadFavorites -> loadFavorites()
            is FavoritesEvent.RemoveFavorite -> removeFavorite(event.productId)
            FavoritesEvent.Retry -> loadFavorites()
            is FavoritesEvent.NotificationPermissionResult -> handlePermissionResult(event.granted)
            FavoritesEvent.RequestNotificationPermission -> requestNotificationPermission()
            FavoritesEvent.DismissNotificationBanner -> {
                _state.update { it.copy(showNotificationBanner = false) }
            }
        }
    }

    private fun checkNotificationPermission() {
        val status = notificationsService.getPermissionStatus()
        _state.update {
            it.copy(
                notificationPermissionStatus = status,
                showNotificationBanner = status == NotificationPermissionStatus.NOT_DETERMINED
            )
        }
    }

    private fun handlePermissionResult(granted: Boolean) {
        val newStatus = if (granted) NotificationPermissionStatus.GRANTED else NotificationPermissionStatus.DENIED
        _state.update {
            it.copy(
                notificationPermissionStatus = newStatus,
                showNotificationBanner = false
            )
        }
        scope.launch {
            notificationsService.onPermissionResult(granted)
        }
    }

    private fun requestNotificationPermission() {
        scope.launch {
            val status = notificationsService.requestPermissionAndRegister()
            _state.update {
                it.copy(
                    notificationPermissionStatus = status,
                    showNotificationBanner = status == NotificationPermissionStatus.NOT_DETERMINED
                )
            }
        }
    }

    private fun loadFavorites() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, error = null) }
        loadJob = scope.launch {
            favoritesService.getFavorites().fold(
                onSuccess = { products ->
                    checkNotificationPermission()
                    _state.update {
                        it.copy(products = mapper.map(products), isLoading = false, error = null)
                    }
                },
                onError = { networkError ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = networkError.toFavoritesError(),
                            showNotificationBanner = false
                        )
                    }
                }
            )
        }
    }

    private fun removeFavorite(productId: String) {
        if (_state.value.writesBlocked) return
        val previousProducts = _state.value.products
        _state.update { it.copy(products = previousProducts.filter { p -> p.id != productId }) }

        scope.launch {
            favoritesService.toggleFavorite(productId, currentlyFavorited = true).fold(
                onSuccess = { },
                onError = { networkError ->
                    _state.update {
                        it.copy(
                            products = previousProducts,
                            error = networkError.toFavoritesError()
                        )
                    }
                    loadFavorites()
                }
            )
        }
    }
}
