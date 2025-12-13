package org.example.fakeshop_clients.features.core.di

import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.example.fakeshop_clients.features.profile.di.mobileProfileModule
private var composeModulesLoaded = false

private val composeAppKoinModules: List<Module> = listOf(
    mobileProfileModule
    // Add other compose modules here
)

fun ensureComposeKoinModulesLoaded() {
    if (!composeModulesLoaded) {
        try {
            loadKoinModules(composeAppKoinModules)
            composeModulesLoaded = true
            println("✅ ComposeApp Koin modules loaded successfully")
        } catch (e: Exception) {
            println("❌ Failed to load ComposeApp modules: ${e.message}")
            throw e
        }
    }
}