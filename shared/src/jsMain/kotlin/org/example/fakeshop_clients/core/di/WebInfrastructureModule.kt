package org.example.fakeshop_clients.core.di

import AxiosClient
import org.example.fakeshop_clients.core.data.ApiClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    single<ApiClient> {
        AxiosClient("http://localhost:8080")
    }

    // Alias for authClient - same instance
    single<ApiClient>(named("authClient")) {
        get<ApiClient>() // Returns the same client
    }
}