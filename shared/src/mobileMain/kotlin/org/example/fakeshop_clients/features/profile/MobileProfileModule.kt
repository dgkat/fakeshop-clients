package org.example.fakeshop_clients.features.profile

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.features.profile.di.profileModule
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewModel
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewStore
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val mobileProfileModule = module {

    includes(profileModule)

    factory<ProfileViewStore> { (scope: CoroutineScope) ->
        ProfileViewStore(scope = scope, profileService = get())
    }

    viewModel {
        ProfileViewModel()
    }
}