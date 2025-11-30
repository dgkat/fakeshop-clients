import kotlinx.browser.document
import kotlinx.browser.window
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.components.SearchButton
import react.create
import react.dom.client.createRoot
import web.dom.Element

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupSearchIsland() {
    console.log("[setupSearchIsland] Setting up search island...")

    try {
        // Initialize Koin

        val viewModel = SearchViewModel()

        val rootElement = document.getElementById("search-island-root") as? Element
            ?: error("Root element not found")

        createRoot(rootElement).render(
            SearchButton.create {
                this.viewModel = viewModel
            }
        )

        console.log("[setupSearchIsland] ✅ Island hydrated successfully")


        window.addEventListener("beforeunload", {
            //stopKoin here
        })
    } catch (e: Exception) {
        console.error("[setupSearchIsland] Error:", e.message)
    }
}