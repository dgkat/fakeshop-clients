package org.example.fakeshop_clients.features.profile.presentation

import kotlinx.coroutines.CoroutineScope
import org.example.fakeshop_clients.core.auth.domain.AuthRepository
import org.example.fakeshop_clients.features.profile.domain.ProfileService
import org.example.fakeshop_clients.features.profile.presentation.components.ProfileView
import org.koin.core.context.GlobalContext
import org.koin.core.qualifier.named
import react.FC
import react.Props
import react.useMemo

val ProfilePage = FC<Props> {
    val koin = GlobalContext.get()
    val viewModel = useMemo {
        try {
            console.log("Getting ProfileViewModel...")

            // Check the appScope specifically
            val appScope = koin.getOrNull<CoroutineScope>(named("appScope"))
            console.log("appScope exists:", appScope)

            // Check ProfileService
            val profileService = koin.getOrNull<ProfileService>()
            console.log("ProfileService exists:", profileService)

            // Try to create ProfileViewStore manually
            if (appScope != null && profileService != null) {
                console.log("Both dependencies exist, trying to create ProfileViewStore...")
                val store = ProfileViewStore(scope = appScope, profileService = profileService)
                console.log("ProfileViewStore created successfully!")
            }

            koin.get<ProfileViewModel>()
        } catch (e: Exception) {
            console.error("Failed to create ProfileViewModel:", e)
            console.error("Error details:", e.message)
            throw e
        }
    }

    ProfileView {
        this.viewModel = viewModel
    }
}