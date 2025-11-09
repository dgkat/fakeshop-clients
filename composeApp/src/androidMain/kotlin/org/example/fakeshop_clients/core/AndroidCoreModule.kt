package org.example.fakeshop_clients.core

import org.example.fakeshop_clients.core.di.androidInfrastructureModule
import org.example.fakeshop_clients.features.home.di.androidHomeModule
import org.example.fakeshop_clients.features.home.di.homeModule

val androidCoreModule = listOf(androidHomeModule, androidInfrastructureModule, homeModule)