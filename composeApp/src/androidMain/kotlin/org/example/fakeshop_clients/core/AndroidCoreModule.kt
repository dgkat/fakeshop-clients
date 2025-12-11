package org.example.fakeshop_clients.core

import org.example.fakeshop_clients.core.auth.di.mobileInfrastructureModule
import org.example.fakeshop_clients.core.di.androidInfrastructureModule
import org.example.fakeshop_clients.features.home.di.androidHomeModule
import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.profile.mobileProfileModule

val androidCoreModule = listOf(
    androidHomeModule,
    androidInfrastructureModule,
    mobileInfrastructureModule,
    homeModule,
    mobileProfileModule
)