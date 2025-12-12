package org.example.fakeshop_clients.features.profile.presentation.components

import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

external interface LoggedInViewProps : Props {
    var isProcessing: Boolean
    var error: String?
    var onLogout: () -> Unit
}

val LoggedInView = FC<LoggedInViewProps> { props ->
    div {
        className = ClassName("logged-in-content")

        h2 {
            className = ClassName("profile-title")
            +"You are logged in"
        }

        props.error?.let { errorMessage ->
            p {
                className = ClassName("error-message")
                +errorMessage
            }
        }

        button {
            className = ClassName("logout-button")
            disabled = props.isProcessing
            onClick = {
                props.onLogout()
            }

            if (props.isProcessing) {
                +"Logging out..."
            } else {
                +"Logout"
            }
        }
    }
}