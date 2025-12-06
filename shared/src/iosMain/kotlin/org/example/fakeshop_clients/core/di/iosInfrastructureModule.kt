package org.example.fakeshop_clients.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.http.Url
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.data.KeychainTokenStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val iosInfrastructureModule = module {
    //TODO update to real url / read from env
    val baseUrl = "http://localhost:8080"
    val parsedUrl = Url(baseUrl)

    single<Url>(named("parsedUrl")) {
        parsedUrl
    }
    single<TokenStorage> {
        KeychainTokenStorage()
    }
    single<HttpClientEngine> {
        Darwin.create()
    }
}