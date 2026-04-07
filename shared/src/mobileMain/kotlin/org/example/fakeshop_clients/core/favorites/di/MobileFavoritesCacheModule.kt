package org.example.fakeshop_clients.core.favorites.di

import org.example.fakeshop_clients.features.favorites.data.FavoritesCache
import org.example.fakeshop_clients.features.favorites.data.MobileFavoritesCache
import org.koin.dsl.module

val mobileFavoritesCacheModule = module {
    single<FavoritesCache> { MobileFavoritesCache() }
}
