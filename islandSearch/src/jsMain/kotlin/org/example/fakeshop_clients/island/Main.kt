package org.example.fakeshop_clients.island


import kotlinx.browser.window

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupIslandModule() {
    console.log("[setupIslandModule] Setting up search island...")

    // Just expose a simple function to window
    window.asDynamic().renderSearchButton = {
        console.log("[renderSearchButton] Creating button")
        val button = js("document.createElement('button')")
        button.textContent = "Search"
        button.onclick = {
            console.log("[SearchButton] Clicked!")
        }
        button
    }

    console.log("[setupIslandModule] Ready")
}