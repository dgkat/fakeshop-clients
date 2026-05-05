package org.example.fakeshop_clients.features.profile.presentation.components

import kotlinx.browser.window
import react.FC
import react.Props
import react.dom.html.ReactHTML.a
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.span
import web.cssom.ClassName

private fun isAndroidDevice(): Boolean =
    window.navigator.userAgent.contains("Android", ignoreCase = true)

val AndroidInstallBanner = FC<Props> {
    if (!isAndroidDevice()) return@FC

    div {
        className = ClassName("android-install-banner")
        div {
            className = ClassName("android-install-banner-icon-box")
            span {
                className = ClassName("android-install-banner-icon-symbol")
                +"⬇"
            }
        }
        div {
            className = ClassName("android-install-banner-text")
            div {
                className = ClassName("android-install-banner-title")
                +"Get the FakeShop app"
            }
            div {
                className = ClassName("android-install-banner-subtitle")
                +"Direct download · APK"
            }
        }
        a {
            className = ClassName("android-install-banner-btn")
            href = "https://github.com/dgkat/fakeshop-android-releases/releases/latest/download/fakeshop.apk"
            span { +"Download" }
        }
    }
}
