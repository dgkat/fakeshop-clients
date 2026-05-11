package org.example.fakeshop_clients.features.profile.presentation.components

import org.example.fakeshop_clients.core.i18n.getString
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsState
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.span
import react.useEffect
import web.cssom.ClassName
import web.html.InputType
import web.html.checkbox

external interface NotificationPrefsSectionProps : Props {
    var state: NotificationPrefsState
    var onLoad: () -> Unit
    var onTogglePriceDrop: (Boolean) -> Unit
}

val NotificationPrefsSection = FC<NotificationPrefsSectionProps> { props ->
    useEffect(Unit) {
        props.onLoad()
    }

    div {
        className = ClassName("profile-card profile-notif-card")

        if (props.state.isLoading) {
            div {
                className = ClassName("profile-notif-loading")
                +"…"
            }
        } else {
            // Price drop toggle
            div {
                className = ClassName("profile-notif-row profile-notif-divider")
                div {
                    className = ClassName("profile-notif-info")
                    div {
                        className = ClassName("profile-notif-title-row")
                        span {
                            className = ClassName("profile-notif-title")
                            +getString("notification_prefs_price_drop")
                        }
                        span {
                            className = ClassName("profile-info-icon")
                            +"ⓘ"
                        }
                    }
                    div {
                        className = ClassName("profile-notif-desc")
                        +"Sale alerts for items on your list."
                    }
                }
                label {
                    className = ClassName("profile-toggle-switch")
                    input {
                        type = InputType.checkbox
                        checked = props.state.priceDropEnabled
                        disabled = props.state.isToggling
                        onChange = { event -> props.onTogglePriceDrop(event.target.checked) }
                    }
                    span { className = ClassName("profile-toggle-slider") }
                }
            }

            // Back-in-stock toggle (placeholder)
            div {
                className = ClassName("profile-notif-row")
                div {
                    className = ClassName("profile-notif-info")
                    div {
                        className = ClassName("profile-notif-title-row")
                        span {
                            className = ClassName("profile-notif-title")
                            +"Back-in-stock alerts"
                        }
                        span {
                            className = ClassName("profile-info-icon")
                            +"ⓘ"
                        }
                    }
                    div {
                        className = ClassName("profile-notif-desc")
                        +"When sold-out favorites return."
                    }
                }
                label {
                    className = ClassName("profile-toggle-switch")
                    input {
                        type = InputType.checkbox
                        checked = false
                        disabled = true
                        onChange = {}
                    }
                    span { className = ClassName("profile-toggle-slider") }
                }
            }
        }
    }
}
