package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.features.profile.di.webProfileModule
import org.example.fakeshop_clients.features.search.di.webSearchModule

val webCoreModule = listOf(
    webCoroutineModule,
    webInfrastructureModule,
    webProfileModule,
    webSearchModule
)