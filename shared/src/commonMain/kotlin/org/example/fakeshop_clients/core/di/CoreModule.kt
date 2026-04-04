package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.features.favorites.di.favoritesModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.recents.di.recentsModule

val coreModule = listOf(homeModule, favoritesModule, recentsModule)