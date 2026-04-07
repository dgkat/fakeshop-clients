package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.core.favorites.di.webFavoritesCacheModule
import org.example.fakeshop_clients.features.favorites.di.webFavoritesModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.profile.di.webProfileModule
import org.example.fakeshop_clients.features.notifications.di.webNotificationsModule
import org.example.fakeshop_clients.features.search.di.webSearchModule

val webCoreModule = listOf(
    webCoroutineModule,
    webInfrastructureModule,
    homeModule,
    webFavoritesCacheModule,
    webProfileModule,
    webSearchModule,
    webFavoritesModule,
    webNotificationsModule
)
