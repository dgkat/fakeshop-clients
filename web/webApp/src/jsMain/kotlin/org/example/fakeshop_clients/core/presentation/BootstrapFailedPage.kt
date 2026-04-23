package org.example.fakeshop_clients.core.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
import org.example.fakeshop_clients.core.i18n.getString
import org.koin.mp.KoinPlatform.getKoin
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.p
import web.cssom.ClassName

val BootstrapFailedPage = FC<Props> {
    div {
        className = ClassName("page-content page-placeholder")
        p { +getString("profile_error_network") }
        button {
            className = ClassName("btn-primary")
            onClick = {
                val koin = getKoin()
                koin.get<CoroutineScope>(AppScopeQualifier).launch {
                    koin.get<SessionBootstrapper>().bootstrap()
                }
            }
            +getString("retry")
        }
    }
}
