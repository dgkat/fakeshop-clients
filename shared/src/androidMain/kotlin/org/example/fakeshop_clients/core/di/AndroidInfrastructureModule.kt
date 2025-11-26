package org.example.fakeshop_clients.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasource
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasourceImpl
import org.example.fakeshop_clients.core.auth.data.AuthTokenProvider
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

    single<HttpClient>(named("publicHttpClient")) {
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

    single<ApiClient>(named("publicClient")) {
        val authHttpClient: HttpClient = get(named("publicHttpClient"))
        KtorClient(authHttpClient)
    }

    single<MobileAuthDatasource> {
        MobileAuthDatasourceImpl(get(named("publicClient")))
    }

    single<ApiClient> {
        val mobileAuthDatasource: MobileAuthDatasource = get()

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
                                val response = mobileAuthDatasource.refreshToken(refreshToken)

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