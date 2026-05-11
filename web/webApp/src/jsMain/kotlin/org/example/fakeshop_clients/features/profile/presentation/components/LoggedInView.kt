package org.example.fakeshop_clients.features.profile.presentation.components

import org.example.fakeshop_clients.core.i18n.getString
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsState
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import web.cssom.ClassName

external interface LoggedInViewProps : Props {
    var isProcessing: Boolean
    var error: String?
    var onLogout: () -> Unit
    var prefsState: NotificationPrefsState
    var onLoadPrefs: () -> Unit
    var onTogglePriceDrop: (Boolean) -> Unit
}

private data class AccountLink(val emoji: String, val title: String, val subtitle: String)

private val accountLinks = listOf(
    AccountLink("🛍️", "My orders", "X active"),
    AccountLink("♥", "My list", "X saved"),
    AccountLink("📍", "Addresses", "0 on file"),
    AccountLink("💳", "Payment methods", "Visa •• XXXX"),
    AccountLink("🎁", "Gift cards & credit", "$0.00"),
    AccountLink("💬", "Help & support", "")
)

val LoggedInView = FC<LoggedInViewProps> { props ->
    div {
        className = ClassName("profile-content")

        // User card
        div {
            className = ClassName("profile-card profile-user-card")
            div {
                className = ClassName("profile-avatar")
                +"👤"
            }
            div {
                className = ClassName("profile-user-info")
                div {
                    className = ClassName("profile-user-name")
                    +"My Account"
                }
                div {
                    className = ClassName("profile-user-subtitle")
                    +"Manage your profile"
                }
            }
            button {
                className = ClassName("profile-edit-btn")
                disabled = true
                +"Edit profile"
            }
            span {
                className = ClassName("profile-chevron profile-chevron-mobile")
                +"›"
            }
        }

        // Account links
        div {
            className = ClassName("profile-section-label")
            +"Account"
        }
        div {
            className = ClassName("profile-card profile-links-card")
            accountLinks.forEach { link ->
                div {
                    className = ClassName("profile-link-item")
                    div {
                        className = ClassName("profile-link-icon-circle")
                        +link.emoji
                    }
                    div {
                        className = ClassName("profile-link-info")
                        div {
                            className = ClassName("profile-link-title")
                            +link.title
                        }
                        if (link.subtitle.isNotEmpty()) {
                            div {
                                className = ClassName("profile-link-subtitle")
                                +link.subtitle
                            }
                        }
                    }
                    span {
                        className = ClassName("profile-chevron")
                        +"›"
                    }
                }
            }
        }

        props.error?.let { errorMessage ->
            p {
                className = ClassName("profile-error-message")
                +errorMessage
            }
        }

        // Notifications
        div {
            className = ClassName("profile-section-label")
            +"Notifications"
        }
        NotificationPrefsSection {
            state = props.prefsState
            onLoad = props.onLoadPrefs
            onTogglePriceDrop = props.onTogglePriceDrop
        }

        // Logout button
        button {
            className = ClassName("profile-logout-btn")
            disabled = props.isProcessing
            onClick = { props.onLogout() }
            if (props.isProcessing) +getString("logging_out")
            else +getString("logout")
        }
    }
}
