package org.example.fakeshop_clients.features.profile.presentation.components

import react.*
import react.dom.html.ReactHTML.div
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsEvent
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsState
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewModel
import org.example.fakeshop_clients.features.profile.presentation.ProfileEvent
import org.example.fakeshop_clients.features.profile.presentation.ProfileState
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewModel
import web.cssom.ClassName

external interface ProfileProps : Props {
    var viewModel: ProfileViewModel?
    var notificationPrefsViewModel: NotificationPrefsViewModel?
}

val ProfileView = FC<ProfileProps> { props ->
    var state by useState(ProfileState(isLoading = true))
    var notificationPrefsState by useState(NotificationPrefsState())

    useEffectWithCleanup(props.viewModel) {
        val vm = props.viewModel
        if (vm != null) {
            val scope = MainScope()
            vm.uiState
                .onEach { newState -> state = newState }
                .launchIn(scope)

            onCleanup { scope.cancel() }
        } else {
            null
        }
    }

    useEffectWithCleanup(props.notificationPrefsViewModel) {
        val vm = props.notificationPrefsViewModel
        if (vm != null) {
            val scope = MainScope()
            vm.state
                .onEach { newState -> notificationPrefsState = newState }
                .launchIn(scope)

            onCleanup { scope.cancel() }
        } else {
            null
        }
    }

    div {
        className = ClassName("profile-container")

        when {
            state.isLoading -> {
                LoadingView()
            }

            state.isLoggedIn -> {
                LoggedInView {
                    isProcessing = state.isProcessing
                    error = state.error.toString()
                    onLogout = {
                        props.viewModel?.onEvent(ProfileEvent.LogoutClicked)
                    }
                    prefsState = notificationPrefsState
                    onLoadPrefs = {
                        props.notificationPrefsViewModel?.onEvent(NotificationPrefsEvent.LoadPreferences)
                    }
                    onTogglePriceDrop = { enabled ->
                        props.notificationPrefsViewModel?.onEvent(NotificationPrefsEvent.TogglePriceDrop(enabled))
                    }
                }
            }

            else -> {
                LoggedOutView {
                    email = state.email
                    password = state.password
                    isProcessing = state.isProcessing
                    error = state.error.toString()
                    onEmailChange = { newEmail ->
                        props.viewModel?.onEvent(ProfileEvent.EmailChanged(newEmail))
                    }
                    onPasswordChange = { newPassword ->
                        props.viewModel?.onEvent(ProfileEvent.PasswordChanged(newPassword))
                    }
                    onLogin = {
                        props.viewModel?.onEvent(ProfileEvent.LoginClicked)
                    }
                    onSignUp = {
                        props.viewModel?.onEvent(ProfileEvent.SignUpClicked)
                    }
                }
            }
        }
    }
}
