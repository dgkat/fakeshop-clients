package org.example.fakeshop_clients.island


import kotlinx.browser.window
import org.example.fakeshop_clients.island.presentation.SearchViewmodel

var searchViewmodel: SearchViewmodel? = null
@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupIslandModule() {
    console.log("[setupIslandModule] Setting up search island...")

    // Initialize the viewmodel
    searchViewmodel = SearchViewmodel()
    console.log("[setupIslandModule] SearchViewmodel created")

    // Expose a function to render the search island
    window.asDynamic().renderSearchIsland = {
        console.log("[renderSearchIsland] Creating search island")

        // Container
        val container = js("document.createElement('div')")
        container.style.display = "flex"
        container.style.gap = "16px"
        container.style.alignItems = "flex-start"

        // Button
        val button = js("document.createElement('button')")
        button.textContent = "Search"
        button.style.padding = "8px 16px"
        button.style.cursor = "pointer"

        // Results list
        val resultsList = js("document.createElement('ul')")
        resultsList.style.listStyle = "none"
        resultsList.style.padding = "0"
        resultsList.style.margin = "0"
        resultsList.style.minWidth = "150px"

        // Update function
        fun updateResults() {
            val state = searchViewmodel?.uiState?.value
            if (state != null) {
                button.textContent = "Search (${state.searchTerm})"

                // Clear and rebuild results list
                resultsList.innerHTML = ""
                for (result in state.results) {
                    val li = js("document.createElement('li')")
                    li.textContent = result
                    li.style.padding = "4px 0"
                    resultsList.appendChild(li)
                }

                console.log("[updateResults] State: ${state.searchTerm}, Results: ${state.results}")
            }
        }

        button.onclick = {
            console.log("[SearchButton] Clicked!")
            searchViewmodel?.onSearchButtonClick()
            updateResults()
        }

        // Initial update
        updateResults()

        // Add to container
        container.appendChild(button)
        container.appendChild(resultsList)

        container
    }

    console.log("[setupIslandModule] Ready")
}