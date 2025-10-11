package org.example.fakeshop_clients.features.home.di

import org.example.fakeshop_clients.features.home.presentation.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidHomeModule = module {

    viewModel<HomeViewModel> {
        HomeViewModel(
            get()
        )
    }
}