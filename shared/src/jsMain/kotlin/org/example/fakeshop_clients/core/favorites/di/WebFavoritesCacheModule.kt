package org.example.fakeshop_clients.core.favorites.di

import org.example.fakeshop_clients.features.favorites.data.FavoritesCache
import org.example.fakeshop_clients.features.favorites.data.WebFavoritesCache
import org.koin.dsl.module

val webFavoritesCacheModule = module {
    single<FavoritesCache> { WebFavoritesCache() }
}
