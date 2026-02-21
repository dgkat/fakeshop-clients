package org.example.fakeshop_clients.features.profile.presentation

import org.example.fakeshop_clients.features.profile.presentation.components.ProfileView
import org.koin.core.context.GlobalContext
import react.FC
import react.Props
import react.useMemo

val ProfilePage = FC<Props> {
    val koin = GlobalContext.get()
    val viewModel = useMemo {
        koin.get<ProfileViewModel>()
    }

    ProfileView {
        this.viewModel = viewModel
    }
}