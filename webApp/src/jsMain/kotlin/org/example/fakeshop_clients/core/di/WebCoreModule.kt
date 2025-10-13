package org.example.fakeshop_clients.core.di

import org.example.fakeshop_clients.features.home.di.homeModule
import org.example.fakeshop_clients.features.home.di.webHomeModule

val webCoreModule = listOf(webHomeModule, homeModule)