package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.concurrency.WebDispatcherProvider
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.AxiosNetworkExceptionMapper
import org.example.fakeshop_clients.core.data.NetworkExceptionMapper
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.data.WebAuthDatasourceImpl
import org.example.fakeshop_clients.core.data.WebAuthRepository
import org.example.fakeshop_clients.core.data.WebLogoutUser
import org.example.fakeshop_clients.core.data.WebSafePublicApiClient
import org.example.fakeshop_clients.core.data.axios.AxiosClient
import org.example.fakeshop_clients.core.data.fetchClient.PublicApiClient
import org.example.fakeshop_clients.core.data.fetchClient.PublicFetchClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webInfrastructureModule = module {
    val baseUrl = "http://localhost:8080"

    single<NetworkExceptionMapper> { AxiosNetworkExceptionMapper() }

    // Fetch-based PublicApiClient for auth operations
    single<PublicApiClient> {
        PublicFetchClient(baseUrl)
    }

    // Web-specific safe public client (wraps fetch-based client)
    single {
        WebSafePublicApiClient(
            publicApiClient = get<PublicApiClient>(),
            exceptionMapper = get()
        )
    }

    // Main AxiosClient (with auth interceptor)
    single<ApiClient>(named("axiosClient")) {
        val webAuthDatasource: WebAuthDatasource = get()
        AxiosClient(baseUrl, webAuthDatasource)
    }

    // Default ApiClient points to AxiosClient
    single<ApiClient> {
        get<ApiClient>(named("axiosClient"))
    }

    single {
        SafeAuthenticatedApiClient(
            client = get<ApiClient>(named("axiosClient")),
            exceptionMapper = get()
        )
    }

    single<WebAuthDatasource> {
        WebAuthDatasourceImpl(get<WebSafePublicApiClient>())
    }

    single<AuthRepository> {
        WebAuthRepository(
            webAuthDatasource = get()
        )
    }

    factory<LogoutUser> { WebLogoutUser(get<SafeAuthenticatedApiClient>()) }

    single<DispatcherProvider> {
        WebDispatcherProvider()
    }
}
