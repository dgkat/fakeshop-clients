package org.example.fakeshop_clients.features.favorites.di

import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewStore
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewModel
import org.example.fakeshop_clients.features.recents.di.recentsModule
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webFavoritesModule = module {
    includes(favoritesModule, recentsModule)

    factory { FavoritesViewStore(scope = get(qualifier = named("appScope")), favoritesService = get(), mapper = get(), notificationsService = get()) }
    factory { RecentsViewStore(scope = get(qualifier = named("appScope")), recentsService = get(), mapper = get()) }
    factory { FavoritesViewModel(favoritesStore = get(), recentsStore = get()) }
}
