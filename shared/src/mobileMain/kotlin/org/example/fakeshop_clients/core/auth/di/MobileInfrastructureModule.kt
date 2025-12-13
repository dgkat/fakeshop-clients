package org.example.fakeshop_clients.core.auth.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.api_client.KtorClient
import org.example.fakeshop_clients.core.auth.data.LogoutUser
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasource
import org.example.fakeshop_clients.core.auth.data.MobileAuthDatasourceImpl
import org.example.fakeshop_clients.core.auth.data.MobileAuthRepository
import org.example.fakeshop_clients.core.auth.data.MobileLogoutUser
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.core.data.ApiClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mobileInfrastructureModule = module {
    single<AuthRepository> {
        MobileAuthRepository(
            mobileAuthDatasource = get(),
            tokenStorage = get()
        )
    }

    single<HttpClient>(named("publicHttpClient")) {

        val parsedUrl: Url = get(named("parsedUrl"))

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
        val tokenStorage: TokenStorage = get()

        KtorClient(
            HttpClient(get<HttpClientEngine>()) {
                val parsedUrl: Url = get(named("parsedUrl"))

                install(ContentNegotiation) {
                    json(Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    })
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
                            val refreshToken = oldTokens?.refreshToken
                                ?: tokenStorage.getRefreshToken()
                                ?: return@refreshTokens null

                            try {
                                val response = mobileAuthDatasource.refreshToken(refreshToken)

                                println("New tokens received: accessToken -> ${response.accessToken} refreshToken -> ${response.refreshToken}")

                                tokenStorage.saveTokens(
                                    accessToken = response.accessToken,
                                    refreshToken = response.refreshToken
                                )

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

    factory<LogoutUser> { MobileLogoutUser(get(), get()) }

}