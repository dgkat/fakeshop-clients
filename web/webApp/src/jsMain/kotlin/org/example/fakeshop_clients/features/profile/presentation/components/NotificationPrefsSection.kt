package org.example.fakeshop_clients.features.profile.presentation.components

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.example.fakeshop_clients.core.i18n.getString
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsEvent
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsState
import org.example.fakeshop_clients.features.notifications.presentation.NotificationPrefsViewStore
import org.koin.mp.KoinPlatform.getKoin
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h3
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import react.useEffectWithCleanup
import react.useMemo
import react.useState
import web.cssom.ClassName
import web.html.InputType

val NotificationPrefsSection = FC<Props> {
    val store = useMemo { getKoin().get<NotificationPrefsViewStore>() }
    var state by useState(NotificationPrefsState())

    useEffectWithCleanup(store) {
        store.onEvent(NotificationPrefsEvent.LoadPreferences)
        val scope = MainScope()
        val job = store.state
            .onEach { newState -> state = newState }
            .launchIn(scope)

        onCleanup { job::cancel }
    }

    div {
        className = ClassName("notification-prefs-section")

        h3 {
            className = ClassName("section-title")
            +getString("notification_prefs_title")
        }

        if (!state.isLoading) {
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
                        checked = state.priceDropEnabled
                        disabled = state.isToggling
                        onChange = { event ->
                            store.onEvent(NotificationPrefsEvent.TogglePriceDrop(event.target.checked))
                        }
                    }
                }
            }
        }
    }
}
