package org.example.fakeshop_clients.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.core.auth.di.mobileInfrastructureModule
import org.example.fakeshop_clients.core.di.iosInfrastructureModule
import org.example.fakeshop_clients.features.favorites.di.favoritesModule
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewStore
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.home.presentation.productList.ProductListViewStore
import org.example.fakeshop_clients.features.notifications.IosPushTokenProvider
import org.example.fakeshop_clients.features.notifications.di.notificationsModule
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.notifications.domain.PushTokenProvider
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

    factory { (scope: CoroutineScope) ->
        ProductListViewStore(
            scope = scope,
            productListService = get(),
            favoritesService = get(),
            mapper = get()
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
            favoritesService = get(),
            briefProductMapper = get(),
            detailedProductMapper = get()
        )
    }

    factory { (scope: CoroutineScope) ->
        FavoritesViewStore(
            scope = scope,
            favoritesService = get(),
            mapper = get()
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
            notificationsService = get()
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
}
