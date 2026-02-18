package org.example.fakeshop_clients.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.http.Url
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.concurrency.AndroidDispatcherProvider
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.data.DataStoreTokenStorage
import org.example.fakeshop_clients.shared.BuildConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module

val androidInfrastructureModule = module {
    val parsedUrl = Url(BuildConfig.BASE_URL)

    single<Url>(named("parsedUrl")) {
        parsedUrl
    }

    single<TokenStorage> {
        DataStoreTokenStorage(context = get(), dispatcherProvider = get())
    }

    single<HttpClientEngine> {
        OkHttp.create()
    }

    single<DispatcherProvider> {
        AndroidDispatcherProvider()
    }
}