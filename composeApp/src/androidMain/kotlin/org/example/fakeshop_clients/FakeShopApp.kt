package org.example.fakeshop_clients

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.SvgDecoder
import org.example.fakeshop_clients.core.androidCoreModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.logger.Level

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
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this@FakeShopApp)
            .components {
                add(SvgDecoder.Factory())
            }
            .build()
    }
}