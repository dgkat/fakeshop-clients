package org.example.fakeshop_clients.core.di

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.Logger
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
import org.example.fakeshop_clients.core.interactions.data.UserDefaultsSessionIdStore
import org.example.fakeshop_clients.core.interactions.domain.DefaultSessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.SessionIdStore
import org.example.fakeshop_clients.core.time.MillisClock
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970
import org.example.fakeshop_clients.core.logging.AppLogger
import org.example.fakeshop_clients.core.logging.IosAppLogger
import org.example.fakeshop_clients.core.logging.NoOpLogger
import org.koin.core.qualifier.named
import org.koin.dsl.module

fun iosInfrastructureModule(baseUrl: String, isDebug: Boolean) = module {
    val parsedUrl = Url(baseUrl)

    single<Url>(named("parsedUrl")) {
        parsedUrl
    }

    // Debug-only Ktor logger, gated on the iOS app's build configuration: Swift's `#if DEBUG`
    // flows in through initKoinIos — the same authoritative dev/prod signal that selects the
    // baseUrl. In release [isDebug] is false, so consumers' getOrNull<Logger>(named("ktorLogger"))
    // resolves to null and logging is stripped.
    if (isDebug) {
        single<Logger>(named("ktorLogger")) {
            object : Logger {
                override fun log(message: String) {
                    println("KtorLogger: $message")
                }
            }
        }
    }

    // Debug builds log to the Xcode console; release builds drop everything via NoOpLogger.
    single<AppLogger> { if (isDebug) IosAppLogger() else NoOpLogger }

    single<TokenStorage> {
        KeychainTokenStorage(dispatcherProvider = get())
    }

    single<InstallIdProvider> {
        IosInstallIdProvider()
    }

    single<MillisClock> { MillisClock { (NSDate().timeIntervalSince1970 * 1000).toLong() } }

    single<SessionIdStore> { UserDefaultsSessionIdStore() }

    single<SessionIdProvider> {
        DefaultSessionIdProvider(
            store = get(),
            generator = { NSUUID().UUIDString },
            clock = get()
        )
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