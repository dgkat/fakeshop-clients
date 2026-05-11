package org.example.fakeshop_clients.features.profile.presentation.components

import org.example.fakeshop_clients.core.i18n.getString
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
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
        className = ClassName("profile-content")

        // Header
        div {
            className = ClassName("profile-welcome-title")
            +getString("welcome")
        }
        div {
            className = ClassName("profile-welcome-subtitle")
            +"Sign in to see your orders and saved items."
        }

        // Fields
        div {
            className = ClassName("profile-field-group")

            label {
                className = ClassName("profile-field-label")
                +getString("email")
            }
            input {
                className = ClassName("profile-field-input")
                type = InputType.email
                placeholder = "you@example.com"
                value = props.email
                disabled = props.isProcessing
                onChange = { event -> props.onEmailChange(event.target.value) }
            }

            label {
                className = ClassName("profile-field-label")
                +getString("password")
            }
            input {
                className = ClassName("profile-field-input")
                type = InputType.password
                placeholder = "••••••••"
                value = props.password
                disabled = props.isProcessing
                onChange = { event -> props.onPasswordChange(event.target.value) }
            }
        }

        div {
            className = ClassName("profile-forgot-row")
            span {
                className = ClassName("profile-forgot-link")
                +"Forgot password?"
            }
        }

        props.error?.let { errorMessage ->
            p {
                className = ClassName("profile-error-message")
                +errorMessage
            }
        }

        // Sign in
        button {
            className = ClassName("profile-primary-btn")
            disabled = props.isProcessing
            onClick = { props.onLogin() }
            if (props.isProcessing) +getString("processing") else +getString("login")
        }

        // Create account
        button {
            className = ClassName("profile-outlined-btn")
            disabled = props.isProcessing
            onClick = { props.onSignUp() }
            +getString("sign_up")
        }

        // OR divider
        div {
            className = ClassName("profile-or-divider")
            div { className = ClassName("profile-divider-line") }
            span {
                className = ClassName("profile-or-label")
                +"OR"
            }
            div { className = ClassName("profile-divider-line") }
        }

        // Social sign in
        div {
            className = ClassName("profile-social-btns")
            button {
                className = ClassName("profile-social-btn")
                disabled = true
                span {
                    className = ClassName("profile-google-g")
                    +"G"
                }
                +"Google"
            }
            button {
                className = ClassName("profile-social-btn")
                disabled = true
                span {
                    className = ClassName("profile-apple-icon")
                    +""
                }
                +"Apple"
            }
        }
    }
}
