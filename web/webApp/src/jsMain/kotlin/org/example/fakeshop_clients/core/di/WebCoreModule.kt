package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.features.profile.di.webProfileModule

val webCoreModule = listOf(
    webCoroutineModule,
    webInfrastructureModule,
    webProfileModule
)