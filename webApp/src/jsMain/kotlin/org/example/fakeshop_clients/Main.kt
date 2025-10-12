package org.example.fakeshop_clients

import kotlinx.browser.document
import kotlinx.browser.window

fun main() {
    window.onload = {
        document.body?.innerHTML = "<h1>Hello from Kotlin/JS!</h1>"
    }
}