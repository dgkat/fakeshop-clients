package org.example.fakeshop_clients.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    single<CoroutineScope>(qualifier = named("appScope")) {
        CoroutineScope(Dispatchers.Main + SupervisorJob())
    }
}