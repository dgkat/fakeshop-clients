package org.example.fakeshop_clients.core.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.WebInstallIdProvider
import org.example.fakeshop_clients.core.auth.data.WebRoleResolver
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.auth.domain.RoleResolver
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionStore
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
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
import org.example.fakeshop_clients.core.network.UrlProvider
import org.example.fakeshop_clients.core.network.WebUrlProvider
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

@JsName("__BACKEND_BASE_URL__")
external val BACKEND_BASE_URL: String

val webInfrastructureModule = module {
    val baseUrl = BACKEND_BASE_URL

    single<UrlProvider> { WebUrlProvider() }

    single { SessionStore() } binds arrayOf(SessionObserver::class, SessionMutator::class)

    single<RoleResolver> { WebRoleResolver(authClient = get(), baseUrl = get()) }

    single<InstallIdProvider> { WebInstallIdProvider() }

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
        AxiosClient(
            baseUrl = baseUrl,
            webAuthDatasource = webAuthDatasource,
            sessionMutator = get(),
            scope = get(AppScopeQualifier)
        )
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
        WebAuthDatasourceImpl(
            publicClient = get<WebSafePublicApiClient>(),
            baseUrl = get()
        )
    }

    single<AuthRepository> {
        WebAuthRepository(
            webAuthDatasource = get()
        )
    }

    factory<LogoutUser> {
        WebLogoutUser(
            authClient = get<SafeAuthenticatedApiClient>(),
            baseUrl = get()
        )
    }

    single<DispatcherProvider> {
        WebDispatcherProvider()
    }

    single<CoroutineScope>(AppScopeQualifier) {
        CoroutineScope(SupervisorJob() + get<DispatcherProvider>().default)
    }
}
