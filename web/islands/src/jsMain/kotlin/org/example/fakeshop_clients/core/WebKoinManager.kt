package org.example.fakeshop_clients.core

import org.example.fakeshop_clients.core.di.webCoroutineModule
import org.example.fakeshop_clients.core.di.webInfrastructureModule
import org.example.fakeshop_clients.features.favorites.di.favoritesModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.product_list.di.productListModule
import org.example.fakeshop_clients.features.recents.di.recentsModule
import org.example.fakeshop_clients.features.search.di.webSearchModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object WebKoinManager {
    private var isInitialized = false

    fun initialize() {
        if (!isInitialized && GlobalContext.getOrNull() == null) {
            //TODO clean up modules and move to respective features
            startKoin {
                modules(
                    webCoroutineModule,
                    webInfrastructureModule,
                    homeModule,
                    productListModule,
                    webSearchModule,
                    favoritesModule,
                    recentsModule,
                )
            }

            isInitialized = true
        }
    }

    fun stop() {
        if (isInitialized) {
            stopKoin()
            isInitialized = false
        }
    }

    fun getKoin() = GlobalContext.get()
}