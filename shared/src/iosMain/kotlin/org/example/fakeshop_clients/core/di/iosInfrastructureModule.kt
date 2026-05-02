package org.example.fakeshop_clients.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.fakeshop_clients.core.auth.data.IosInstallIdProvider
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.concurrency.IosDispatcherProvider
import org.example.fakeshop_clients.core.data.KeychainTokenStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun iosInfrastructureModule(baseUrl: String) = module {
    val parsedUrl = Url(baseUrl)

    single<Url>(named("parsedUrl")) {
        parsedUrl
    }
    single<TokenStorage> {
        KeychainTokenStorage(dispatcherProvider = get())
    }

    single<InstallIdProvider> {
        IosInstallIdProvider()
    }
    single<HttpClientEngine> {
        Darwin.create()
    }

    single<DispatcherProvider> {
        IosDispatcherProvider()
    }

    single<CoroutineScope>(AppScopeQualifier) {
        CoroutineScope(SupervisorJob() + get<DispatcherProvider>().default)
    }
}