package org.example.fakeshop_clients.features.profile.di

import org.example.fakeshop_clients.features.profile.presentation.ProfileViewModel
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewStore
import org.koin.core.qualifier.named
import org.koin.dsl.module

val webProfileModule = module {
    includes(profileModule)

    factory {
        ProfileViewStore(
            scope = get(qualifier = named("appScope")),
            profileService = get(),
            favoritesService = get(),
            notificationsService = get(),
            sessionMutator = get(),
            sessionObserver = get()
        )
    }

    factory { ProfileViewModel(store = get()) }
}
