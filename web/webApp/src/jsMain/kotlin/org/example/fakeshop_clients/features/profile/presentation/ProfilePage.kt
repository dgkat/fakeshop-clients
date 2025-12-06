package org.example.fakeshop_clients.features.profile.presentation

import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.h2
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.span
import web.cssom.ClassName

val ProfilePage = FC<Props> {
    div {
        className = ClassName("page-content page-placeholder")

        span {
            className = ClassName("placeholder-icon")
            +"👤"
        }
        h2 { +"Your Profile" }
        p { +"Manage your account" }
    }
}