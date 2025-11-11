package org.example.fakeshop_clients.core.di

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.auth.data.AuthTokenProvider
import org.example.fakeshop_clients.core.auth.data.models.RefreshTokenRequest
import org.example.fakeshop_clients.core.auth.data.models.TokenRefreshResponse
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.KtorClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidInfrastructureModule = module {

    val baseUrl = "http://10.0.2.2:8080"
    val parsedUrl = Url(baseUrl)

    single<HttpClientEngine> {
        OkHttp.create()
    }

    single<HttpClient>(named("authHttpClient")) {
        HttpClient(get<HttpClientEngine>()) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }

            defaultRequest {
                url.protocol = parsedUrl.protocol
                url.host = parsedUrl.host
                url.port = parsedUrl.port
            }
        }
    }

    single<ApiClient>(named("authClient")) {
        val authHttpClient: HttpClient = get(named("authHttpClient"))
        KtorClient(authHttpClient)
    }

    single<ApiClient> {
        val authHttpClient: HttpClient = get(named("authHttpClient"))

        KtorClient(
            HttpClient(get<HttpClientEngine>()) {
                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
                }
                install(Auth) {
                    bearer {
                        loadTokens {
                            AuthTokenProvider.accessToken?.let {
                                BearerTokens(
                                    accessToken = it,
                                    refreshToken = AuthTokenProvider.refreshToken ?: ""
                                )
                            }
                        }

                        refreshTokens {
                            val refreshToken = oldTokens?.refreshToken
                                ?: AuthTokenProvider.refreshToken
                                ?: return@refreshTokens null

                            try {
                                val response: TokenRefreshResponse = authHttpClient.post("/api/auth/refresh") {
                                    markAsRefreshTokenRequest()
                                    contentType(ContentType.Application.Json)
                                    setBody(RefreshTokenRequest(refreshToken))
                                }.body<TokenRefreshResponse>()
                                println("New tokens received: accessToken -> ${response.accessToken} refreshToken -> ${response.refreshToken}")

                                AuthTokenProvider.accessToken = response.accessToken
                                AuthTokenProvider.refreshToken = response.refreshToken

                                BearerTokens(
                                    accessToken = response.accessToken,
                                    refreshToken = response.refreshToken
                                )
                            } catch (e: Exception) {
                                println("Token refresh failed: ${e.message}")
                                null
                            }
                        }
                    }
                }
                defaultRequest {
                    url.protocol = parsedUrl.protocol
                    url.host = parsedUrl.host
                    url.port = parsedUrl.port
                }
            }
        )
    }
}