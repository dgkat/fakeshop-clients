package org.example.fakeshop_clients.features.profile.presentation.components

import react.*
import react.dom.html.ReactHTML.div
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.features.profile.presentation.ProfileEvent
import org.example.fakeshop_clients.features.profile.presentation.ProfileState
import org.example.fakeshop_clients.features.profile.presentation.ProfileViewModel
import web.cssom.ClassName

external interface ProfileProps : Props {
    var viewModel: ProfileViewModel?
}

val ProfileView = FC<ProfileProps> { props ->
    var state by useState(ProfileState(isLoading = true))

    useEffectWithCleanup(props.viewModel) {
        if (props.viewModel != null) {
            val scope = MainScope()
            val job = props.viewModel!!.uiState
                .onEach { newState ->
                    state = newState
                }
                .launchIn(scope)

            onCleanup { job::cancel }
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
                    error = state.error
                    onLogout = {
                        props.viewModel?.onEvent(ProfileEvent.LogoutClicked)
                    }
                }
            }

            else -> {
                LoggedOutView {
                    email = state.email
                    password = state.password
                    isProcessing = state.isProcessing
                    error = state.error
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