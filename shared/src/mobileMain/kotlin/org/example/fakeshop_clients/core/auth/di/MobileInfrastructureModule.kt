package org.example.fakeshop_clients.core.auth.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.api_client.KtorClient
import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasource
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasourceImpl
import org.example.fakeshop_clients.core.auth.data.MobileAuthRepository
import org.example.fakeshop_clients.core.auth.data.MobileLogoutUser
import org.example.fakeshop_clients.core.auth.data.MobileRoleResolver
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.RoleResolver
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionStore
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.KtorNetworkExceptionMapper
import org.example.fakeshop_clients.core.data.NetworkExceptionMapper
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.data.SafePublicApiClient
import org.example.fakeshop_clients.core.data.post
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.core.favorites.di.mobileFavoritesCacheModule
import org.example.fakeshop_clients.core.network.MobileUrlProvider
import org.example.fakeshop_clients.core.network.UrlProvider
import org.koin.core.qualifier.named
import org.koin.dsl.binds
import org.koin.dsl.module

val mobileInfrastructureModule = module {

    includes(mobileFavoritesCacheModule)

    single<UrlProvider> { MobileUrlProvider() }

    single<NetworkExceptionMapper> { KtorNetworkExceptionMapper() }

    single { SessionStore() } binds arrayOf(SessionObserver::class, SessionMutator::class)

    single<RoleResolver> { MobileRoleResolver(tokenStorage = get()) }

    single {
        SessionBootstrapper(
            roleResolver = get(),
            authRepository = get(),
            installIdProvider = get(),
            sessionMutator = get(),
            sessionObserver = get()
        )
    }

    // Public HttpClient (no auth)
    single<HttpClient>(named("publicHttpClient")) {
        val parsedUrl: Url = get(named("parsedUrl"))
        val ktorLogger = getOrNull<Logger>(named("ktorLogger"))

        HttpClient(get<HttpClientEngine>()) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            ktorLogger?.let {
                install(Logging) {
                    logger = it
                    level = LogLevel.ALL
                }
            }
            defaultRequest {
                url.protocol = parsedUrl.protocol
                url.host = parsedUrl.host
                url.port = parsedUrl.port
            }
        }
    }

    // Authenticated HttpClient (with bearer token + refresh)
    single<HttpClient>(named("authHttpClient")) {
        val parsedUrl: Url = get(named("parsedUrl"))
        val tokenStorage: TokenStorage = get()
        val publicApiClient: SafePublicApiClient = get()
        val authDatasource: MobileAuthDatasource = get()
        val installIdProvider: InstallIdProvider = get()
        val sessionMutator: SessionMutator = get()
        val ktorLogger = getOrNull<Logger>(named("ktorLogger"))

        HttpClient(get<HttpClientEngine>()) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            ktorLogger?.let {
                install(Logging) {
                    logger = it
                    level = LogLevel.ALL
                }
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        tokenStorage.getAccessToken()?.let {
                            BearerTokens(
                                accessToken = it,
                                refreshToken = null
                            )
                        }
                    }

                    refreshTokens {
                        refreshTokensSafely(tokenStorage, publicApiClient, authDatasource, installIdProvider, sessionMutator, oldTokens)
                    }
                }
            }
            defaultRequest {
                url.protocol = parsedUrl.protocol
                url.host = parsedUrl.host
                url.port = parsedUrl.port
            }
        }
    }

    // Low-level ApiClient wrappers (KtorClient)
    single<ApiClient>(named("publicApiClient")) {
        KtorClient(get(named("publicHttpClient")))
    }

    single<ApiClient>(named("authApiClient")) {
        KtorClient(get(named("authHttpClient")))
    }

    single {
        SafePublicApiClient(
            client = get<ApiClient>(named("publicApiClient")),
            exceptionMapper = get()
        )
    }

    single {
        SafeAuthenticatedApiClient(
            client = get<ApiClient>(named("authApiClient")),
            exceptionMapper = get()
        )
    }

    single<MobileAuthDatasource> {
        MobileAuthDatasourceImpl(
            publicClient = get<SafePublicApiClient>(),
            baseUrl = get()
        )
    }

    single<AuthRepository> {
        MobileAuthRepository(
            mobileAuthDatasource = get(),
            tokenStorage = get()
        )
    }

    factory<LogoutUser> {
        MobileLogoutUser(
            authClient = get(),
            tokenStorage = get(),
            baseUrl = get()
        )
    }
}

// TODO move to separate file
// TODO: Update to use UrlProvider when refactoring refresh token mechanism
private suspend fun refreshTokensSafely(
    tokenStorage: TokenStorage,
    publicApiClient: SafePublicApiClient,
    authDatasource: MobileAuthDatasource,
    installIdProvider: InstallIdProvider,
    sessionMutator: SessionMutator,
    oldTokens: BearerTokens?
): BearerTokens? {
    val refreshToken = oldTokens?.refreshToken
        ?: tokenStorage.getRefreshToken()
        ?: return fallbackToGuest(tokenStorage, authDatasource, installIdProvider, sessionMutator)

    return publicApiClient.post<TokenRefreshResponse, RefreshTokenRequest>(
        path = "/api/mobile/auth/refresh",
        body = RefreshTokenRequest(refreshToken)
    ).fold(
        onSuccess = { response ->
            tokenStorage.saveTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
            BearerTokens(
                accessToken = response.accessToken,
                refreshToken = response.refreshToken
            )
        },
        onError = {
            fallbackToGuest(tokenStorage, authDatasource, installIdProvider, sessionMutator)
        }
    )
}

private suspend fun fallbackToGuest(
    tokenStorage: TokenStorage,
    authDatasource: MobileAuthDatasource,
    installIdProvider: InstallIdProvider,
    sessionMutator: SessionMutator
): BearerTokens? {
    val installId = installIdProvider.get()
    return authDatasource.guest(installId).fold(
        onSuccess = { response ->
            tokenStorage.replaceTokens(response.accessToken, response.refreshToken)
            sessionMutator.setAuthenticated(Role.GUEST)
            BearerTokens(accessToken = response.accessToken, refreshToken = response.refreshToken)
        },
        onError = {
            sessionMutator.setBootstrapFailed()
            null
        }
    )
}
