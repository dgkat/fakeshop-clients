package org.example.fakeshop_clients.features.profile.presentation.components

import react.*
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.p
import org.example.fakeshop_clients.core.strings.Strings
import web.cssom.ClassName
import web.html.InputType
import web.html.email
import web.html.password

external interface LoggedOutViewProps : Props {
    var email: String
    var password: String
    var isProcessing: Boolean
    var error: String?
    var onEmailChange: (String) -> Unit
    var onPasswordChange: (String) -> Unit
    var onLogin: () -> Unit
    var onSignUp: () -> Unit
}

val LoggedOutView = FC<LoggedOutViewProps> { props ->
    div {
        className = ClassName("logged-out-content")

        h2 {
            className = ClassName("profile-title")
            +Strings.WELCOME
        }

        div {
            className = ClassName("form-container")

            input {
                className = ClassName("form-input")
                type = InputType.email
                placeholder = Strings.EMAIL
                value = props.email
                disabled = props.isProcessing
                onChange = { event ->
                    props.onEmailChange(event.target.value)
                }
            }

            input {
                className = ClassName("form-input")
                type = InputType.password
                placeholder = Strings.PASSWORD
                value = props.password
                disabled = props.isProcessing
                onChange = { event ->
                    props.onPasswordChange(event.target.value)
                }
            }

            props.error?.let { errorMessage ->
                p {
                    className = ClassName("error-message")
                    +errorMessage
                }
            }

            div {
                className = ClassName("button-row")

                button {
                    className = ClassName("login-button primary-button")
                    disabled = props.isProcessing
                    onClick = {
                        props.onLogin()
                    }

                    if (props.isProcessing) {
                        +Strings.PROCESSING
                    } else {
                        +Strings.LOGIN
                    }
                }

                button {
                    className = ClassName("signup-button secondary-button")
                    disabled = props.isProcessing
                    onClick = {
                        props.onSignUp()
                    }

                    +Strings.SIGN_UP
                }
            }
        }
    }
}