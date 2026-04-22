package org.example.fakeshop_clients

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.androidCoreModule
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus
import org.example.fakeshop_clients.features.notifications.domain.NotificationsService
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level
import org.koin.mp.KoinPlatform.getKoin

class FakeShopApp : Application() , ImageLoaderFactory{

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@FakeShopApp)
            modules(
                androidCoreModule
            )
        }

        createNotificationChannels()
        bootstrapSession()
    }

    private fun bootstrapSession() {
        val appScope = getKoin().get<CoroutineScope>(AppScopeQualifier)
        appScope.launch {
            val profileService = getKoin().get<ProfileService>()
            val sessionMutator = getKoin().get<SessionMutator>()

            val isLoggedIn = profileService.checkLoginStatus()
                .let { result ->
                    (result as? org.example.fakeshop_clients.core.error_handling.Result.Success)?.data ?: false
                }
            // TODO(step-5): replace with SessionBootstrapper.
            if (isLoggedIn) sessionMutator.setAuthenticated(Role.LOGGED_USER)
            else sessionMutator.setAuthenticated(Role.GUEST)

            val service = getKoin().get<NotificationsService>()
            if (isLoggedIn && service.getPermissionStatus() == NotificationPermissionStatus.GRANTED) {
                service.registerDeviceAfterAuth()
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PRICE_DROPS_CHANNEL_ID,
                "Price Drop Alerts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for price drops on your favorite products"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    companion object {
        const val PRICE_DROPS_CHANNEL_ID = "price_drops"
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this@FakeShopApp)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}