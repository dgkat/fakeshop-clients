package org.example.fakeshop_clients.core

import org.example.fakeshop_clients.core.di.webInfrastructureModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.product_list.di.productListModule
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object WebKoinManager {
    private var isInitialized = false

    fun initialize() {
        if (!isInitialized && GlobalContext.getOrNull() == null) {
            console.log("[WebKoinManager] Initializing Koin...")

            //TODO clean up modules and move to respective features
            startKoin {
                modules(
                    webInfrastructureModule,
                    homeModule,
                    productListModule,
                )
            }

            isInitialized = true
            console.log("[WebKoinManager] ✅ Koin initialized successfully")
        } else {
            console.log("[WebKoinManager] Koin already initialized")
        }
    }

    fun stop() {
        if (isInitialized) {
            console.log("[WebKoinManager] Stopping Koin...")
            stopKoin()
            isInitialized = false
        }
    }

    fun getKoin() = GlobalContext.get()
}