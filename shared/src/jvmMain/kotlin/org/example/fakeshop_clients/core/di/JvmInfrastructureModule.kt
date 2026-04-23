package org.example.fakeshop_clients.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.api_client.KtorClient
import org.example.fakeshop_clients.core.auth.SSRGuestDatasource
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.concurrency.JvmDispatcherProvider
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.KtorNetworkExceptionMapper
import org.example.fakeshop_clients.core.data.NetworkExceptionMapper
import org.example.fakeshop_clients.core.data.SSRSafeApiClient
import org.example.fakeshop_clients.core.data.SafeAuthenticatedApiClient
import org.example.fakeshop_clients.core.network.JvmUrlProvider
import org.example.fakeshop_clients.core.network.UrlProvider
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * JVM infrastructure module for SSR server.
 * Provides HTTP client, API client wrappers, and dispatcher provider.
 */
val jvmInfrastructureModule = module {

    single<UrlProvider> { JvmUrlProvider() }

    single<NetworkExceptionMapper> { KtorNetworkExceptionMapper() }

    // Public HttpClient (no auth) for fetching public data from backend API
    single<HttpClient>(named("publicHttpClient")) {
        val isDevelopment = System.getProperty("io.ktor.development")?.toBoolean() ?: false
        val backendUrl = Url(
            System.getenv("BACKEND_BASE_URL")
                ?: if (isDevelopment) "http://localhost:8080"
                else error("BACKEND_BASE_URL environment variable is required in production")
        )
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            // Install cookies plugin to handle cookie forwarding
            install(HttpCookies)

            defaultRequest {
                url {
                    protocol = backendUrl.protocol
                    host = backendUrl.host
                    port = backendUrl.port
                }
            }
        }
    }

    // ApiClient wrapper
    single<ApiClient>(named("publicApiClient")) {
        KtorClient(get(named("publicHttpClient")))
    }

    // Safe authenticated client (uses public client for now, can be extended for auth later)
    single {
        SafeAuthenticatedApiClient(
            client = get<ApiClient>(named("publicApiClient")),
            exceptionMapper = get()
        )
    }

    // SSR-specific safe API client (accepts cookies per-request)
    single {
        SSRSafeApiClient(
            httpClient = get(named("publicHttpClient")),
            exceptionMapper = get()
        )
    }

    // Minimal client without HttpCookies plugin — used only for SSR guest bootstrap
    // so the raw Set-Cookie header from the backend response remains accessible.
    single<HttpClient>(named("guestHttpClient")) {
        val isDevelopment = System.getProperty("io.ktor.development")?.toBoolean() ?: false
        val backendUrl = Url(
            System.getenv("BACKEND_BASE_URL")
                ?: if (isDevelopment) "http://localhost:8080"
                else error("BACKEND_BASE_URL environment variable is required in production")
        )
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            expectSuccess = false
            defaultRequest {
                url {
                    protocol = backendUrl.protocol
                    host = backendUrl.host
                    port = backendUrl.port
                }
            }
        }
    }

    single { SSRGuestDatasource(get(named("guestHttpClient"))) }

    single<DispatcherProvider> {
        JvmDispatcherProvider()
    }
}