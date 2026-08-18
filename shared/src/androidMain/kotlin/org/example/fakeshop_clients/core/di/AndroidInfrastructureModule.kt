package org.example.fakeshop_clients.core.di

import android.util.Log
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.Logger
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.example.fakeshop_clients.core.auth.data.AndroidInstallIdProvider
import org.example.fakeshop_clients.core.auth.data.TokenStorage
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.concurrency.AndroidDispatcherProvider
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.core.concurrency.DispatcherProvider
import org.example.fakeshop_clients.core.data.DataStoreTokenStorage
import org.example.fakeshop_clients.core.interactions.data.DataStoreSessionIdStore
import org.example.fakeshop_clients.core.interactions.domain.DefaultSessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionIdStore
import org.example.fakeshop_clients.core.time.MillisClock
import org.example.fakeshop_clients.core.logging.AndroidAppLogger
import org.example.fakeshop_clients.core.logging.AppLogger
import org.example.fakeshop_clients.core.logging.NoOpLogger
import org.example.fakeshop_clients.shared.BuildConfig
import org.koin.core.qualifier.named
import org.koin.dsl.module
import java.util.UUID

val androidInfrastructureModule = module {
    val parsedUrl = Url(BuildConfig.BASE_URL)

    single<Url>(named("parsedUrl")) {
        parsedUrl
    }

    if (BuildConfig.IS_DEBUG) {
        single<Logger>(named("ktorLogger")) {
            object : Logger {
                override fun log(message: String) {
                    Log.d("KtorLogger", message)
                }
            }
        }
    }

    single<AppLogger> { if (BuildConfig.IS_DEBUG) AndroidAppLogger() else NoOpLogger }

    single<TokenStorage> {
        DataStoreTokenStorage(context = get(), dispatcherProvider = get(), logger = get())
    }

    single<InstallIdProvider> {
        AndroidInstallIdProvider(context = get(), dispatcherProvider = get())
    }

    single<MillisClock> { MillisClock { System.currentTimeMillis() } }

    single<SessionIdStore> {
        DataStoreSessionIdStore(context = get(), dispatcherProvider = get())
    }

    single<SessionIdProvider> {
        DefaultSessionIdProvider(
            store = get(),
            generator = { UUID.randomUUID().toString() },
            clock = get()
        )
    }

    single<HttpClientEngine> {
        OkHttp.create()
    }

    single<DispatcherProvider> {
        AndroidDispatcherProvider()
    }

    single<CoroutineScope>(AppScopeQualifier) {
        CoroutineScope(SupervisorJob() + get<DispatcherProvider>().default)
    }
}