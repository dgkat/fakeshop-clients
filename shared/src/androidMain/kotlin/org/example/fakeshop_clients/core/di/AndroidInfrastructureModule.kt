package org.example.fakeshop_clients.core.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.KtorClient
import org.koin.dsl.module

val androidInfrastructureModule = module {
    //TODO update to real url
    val testUrl = "https://api.restful-api.dev"

    val parsedUrl = Url(testUrl)

    single<ApiClient> {
        KtorClient(
            HttpClient(OkHttp) {
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
        )
    }
}