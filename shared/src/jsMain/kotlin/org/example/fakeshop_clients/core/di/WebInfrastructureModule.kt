package org.example.fakeshop_clients.core.di

import AxiosClient
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.data.WebAuthDatasourceImpl
import org.example.fakeshop_clients.core.data.WebAuthRepository
import org.example.fakeshop_clients.core.data.axios.PublicAxiosCLient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    val baseUrl = "http://localhost:8080"

    single<ApiClient>(named("publicClient")) {
        PublicAxiosCLient(baseUrl)
    }

    single<WebAuthDatasource> {
        WebAuthDatasourceImpl(get(named("publicClient")))
    }

    single<ApiClient> {
        val webAuthDatasource: WebAuthDatasource = get()
        AxiosClient(baseUrl, webAuthDatasource)
    }

        single<AuthRepository> {
            WebAuthRepository(
                webAuthDatasource = get()
            )
        }
}