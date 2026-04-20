package org.example.fakeshop_clients.core

import org.example.fakeshop_clients.core.auth.di.mobileInfrastructureModule
import org.example.fakeshop_clients.core.di.androidInfrastructureModule
import org.example.fakeshop_clients.features.favorites.di.androidFavoritesModule
import org.example.fakeshop_clients.features.home.di.androidHomeModule
import org.example.fakeshop_clients.features.product_detail.di.androidProductDetailModule
import org.example.fakeshop_clients.features.profile.di.mobileProfileModule
import org.example.fakeshop_clients.features.notifications.di.androidNotificationsModule
import org.example.fakeshop_clients.features.search_bar.di.androidSearchModule

val androidCoreModule = listOf(

    androidInfrastructureModule,
    mobileInfrastructureModule,
    androidHomeModule,
    androidSearchModule,
    androidProductDetailModule,
    mobileProfileModule,
    androidFavoritesModule,
    androidNotificationsModule
)
