package org.example.fakeshop_clients.features.favorites.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewModel
import org.example.fakeshop_clients.features.favorites.presentation.FavoritesViewStore
import org.example.fakeshop_clients.features.recents.di.recentsModule
import org.example.fakeshop_clients.features.recents.presentation.RecentsViewStore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidFavoritesModule = module {

    includes(favoritesModule, recentsModule)

    factory<FavoritesViewStore> { (scope: CoroutineScope) ->
        FavoritesViewStore(scope = scope, favoritesService = get(), mapper = get(), notificationsService = get())
    }

    factory<RecentsViewStore> { (scope: CoroutineScope) ->
        RecentsViewStore(scope = scope, recentsService = get(), mapper = get())
    }

    viewModel {
        FavoritesViewModel()
    }
}
