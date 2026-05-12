package org.example.fakeshop_clients.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.core.auth.di.mobileInfrastructureModule
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.auth.domain.isLoggedIn
import org.example.fakeshop_clients.core.di.iosInfrastructureModule
import org.example.fakeshop_clients.features.favorites.di.favoritesModule
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewStore
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.example.fakeshop_clients.features.notifications.IosPushTokenProvider
import org.example.fakeshop_clients.features.notifications.data.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.data.PushTokenProvider
import org.example.fakeshop_clients.features.notifications.di.notificationsModule
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.NotificationsServiceImpl
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.example.fakeshop_clients.features.productDetail.di.productDetailModule
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailViewStore
import org.example.fakeshop_clients.features.profile.di.profileModule
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.recents.di.recentsModule
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore
import org.example.fakeshop_clients.features.search.di.searchModule
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val iosModule = module {
    single<PushTokenProvider> { IosPushTokenProvider() }

    single<NotificationsService> { get<NotificationsServiceImpl>() }

    single<NotificationPermissionManager> {
        object : NotificationPermissionManager {
            override fun getPermissionStatus() = NotificationPermissionStatus.NOT_DETERMINED
            override suspend fun requestPermission() = NotificationPermissionStatus.NOT_DETERMINED
        }
    }

    factory { (scope: CoroutineScope) ->
        ProductListViewStore(
            scope = scope,
            productListService = get(),
            favoritesService = get(),
            mapper = get(),
            sessionObserver = get()
        )
    }

    factory { (scope: CoroutineScope)->
        SearchViewStore(
            scope = scope,
            searchService = get()
        )
    }

    factory { (scope: CoroutineScope) ->
        ProductDetailViewStore(
            scope = scope,
            productDetailService = get(),
            bduiTemplateService = get(),
            favoritesService = get(),
            briefProductMapper = get(),
            detailedProductMapper = get(),
            sessionObserver = get()
        )
    }

    factory { (scope: CoroutineScope) ->
        FavoritesViewStore(
            scope = scope,
            favoritesService = get(),
            mapper = get(),
            notificationsService = get(),
            sessionObserver = get()
        )
    }

    factory { (scope: CoroutineScope) ->
        RecentsViewStore(
            scope = scope,
            recentsService = get(),
            mapper = get()
        )
    }

    factory { (scope: CoroutineScope) ->
        NotificationPrefsViewStore(
            scope = scope,
            notificationsService = get(),
            sessionObserver = get()
        )
    }
}

fun initKoinIos(baseUrl: String) = startKoin {
    modules(
        iosInfrastructureModule(baseUrl),
        mobileInfrastructureModule,
        homeModule,
        searchModule,
        productDetailModule,
        profileModule,
        favoritesModule,
        recentsModule,
        notificationsModule,
        iosModule
    )
}

class IOSKoinHelper : KoinComponent {
    fun getProductListViewStore(scope: CoroutineScope): ProductListViewStore {
        return get { parametersOf(scope) }
    }

    fun getSearchViewStore(scope: CoroutineScope): SearchViewStore {
        return get { parametersOf(scope) }
    }

    fun getProductDetailViewStore(scope: CoroutineScope): ProductDetailViewStore {
        return get { parametersOf(scope) }
    }

    fun getFavoritesViewStore(scope: CoroutineScope): FavoritesViewStore {
        return get { parametersOf(scope) }
    }

    fun getRecentsViewStore(scope: CoroutineScope): RecentsViewStore {
        return get { parametersOf(scope) }
    }

    fun getProfileService(): ProfileService {
        return get()
    }

    fun getNotificationsService(): NotificationsService {
        return get()
    }

    fun getNotificationPrefsViewStore(scope: CoroutineScope): NotificationPrefsViewStore {
        return get { parametersOf(scope) }
    }

    fun getSessionMutator(): SessionMutator {
        return get()
    }

    fun getSessionObserver(): SessionObserver {
        return get()
    }

    fun getSessionBootstrapper(): SessionBootstrapper {
        return get()
    }

    fun isSessionRealUser(): Boolean {
        val state = get<SessionObserver>().state.value
        return state is SessionState.Authenticated && state.role.isLoggedIn
    }
}
