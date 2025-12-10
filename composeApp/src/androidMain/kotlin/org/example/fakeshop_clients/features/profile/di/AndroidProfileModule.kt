package org.example.fakeshop_clients.features.profile.di

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewModel
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewStore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val androidProfileModule = module {

    includes(profileModule)

    factory<ProfileViewStore> { (scope: CoroutineScope) ->
        ProfileViewStore(scope = scope, profileService = get())
    }

    viewModel {
        ProfileViewModel()
    }
}