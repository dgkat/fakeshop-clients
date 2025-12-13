package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.data.WebAuthDatasourceImpl
import org.example.fakeshop_clients.core.data.WebAuthRepository
import org.example.fakeshop_clients.core.data.WebLogoutUser
import org.example.fakeshop_clients.core.data.axios.AxiosClient
import org.example.fakeshop_clients.core.data.fetchClient.PublicApiClient
import org.example.fakeshop_clients.core.data.fetchClient.PublicFetchClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    val baseUrl = "http://localhost:8080"

    single<PublicApiClient> {
        PublicFetchClient(baseUrl)
    }

    single<WebAuthDatasource> {
        WebAuthDatasourceImpl(get())
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

    factory<LogoutUser> { WebLogoutUser(get()) }
}