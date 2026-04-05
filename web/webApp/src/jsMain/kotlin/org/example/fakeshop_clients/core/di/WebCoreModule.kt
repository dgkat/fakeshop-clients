package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.features.favorites.di.webFavoritesModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.profile.di.webProfileModule
import org.example.fakeshop_clients.features.search.di.webSearchModule

val webCoreModule = listOf(
    webCoroutineModule,
    webInfrastructureModule,
    homeModule,
    webProfileModule,
    webSearchModule,
    webFavoritesModule
)
