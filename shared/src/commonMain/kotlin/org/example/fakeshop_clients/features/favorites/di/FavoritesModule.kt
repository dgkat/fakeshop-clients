package org.example.fakeshop_clients.features.favorites.di

import org.example.fakeshop_clients.features.favorites.data.FavoritesDatasource
import org.example.fakeshop_clients.features.favorites.data.FavoritesDatasourceImpl
import org.example.fakeshop_clients.features.favorites.data.FavoritesRepositoryImpl
import org.example.fakeshop_clients.features.favorites.domain.FavoritesRepository
import org.example.fakeshop_clients.features.favorites.domain.FavoritesService
import org.example.fakeshop_clients.features.favorites.domain.FavoritesServiceImpl
import org.koin.dsl.module

val favoritesModule = module {

    factory<FavoritesDatasource> {
        FavoritesDatasourceImpl(
            authClient = get(),
            baseUrl = get()
        )
    }

    factory<FavoritesRepository> {
        FavoritesRepositoryImpl(
            datasource = get(),
            briefProductMapper = get()
        )
    }

    factory<FavoritesService> {
        FavoritesServiceImpl(
            repository = get()
        )
    }
}
