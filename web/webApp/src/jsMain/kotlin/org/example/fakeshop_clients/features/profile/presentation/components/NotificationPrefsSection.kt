package org.example.fakeshop_clients.features.profile.presentation.components

import org.example.fakeshop_clients.core.i18n.getString
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsState
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
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
        className = ClassName("notification-prefs-section")

        h3 {
            className = ClassName("section-title")
            +getString("notification_prefs_title")
        }

        if (!props.state.isLoading) {
            div {
                className = ClassName("pref-toggle")

                label {
                    className = ClassName("pref-toggle-label")

                    div {
                        span {
                            className = ClassName("pref-label")
                            +getString("notification_prefs_price_drop")
                        }
                        p {
                            className = ClassName("pref-description")
                            +getString("notification_prefs_price_drop_desc")
                        }
                    }

                    input {
                        type = InputType.checkbox
                        checked = props.state.priceDropEnabled
                        disabled = props.state.isToggling
                        onChange = { event ->
                            props.onTogglePriceDrop(event.target.checked)
                        }
                    }
                }
            }
        }
    }
}
