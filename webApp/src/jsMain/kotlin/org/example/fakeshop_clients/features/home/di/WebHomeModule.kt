package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.features.home.presentation.HomeViewmodel
import org.koin.dsl.module

val webHomeModule = module {
    factory { HomeViewmodel() }
}