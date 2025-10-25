import kotlinx.browser.document
import kotlinx.browser.window
import org.example.fakeshop_clients.features.product_list.presentation.ProductListViewmodel
import org.example.fakeshop_clients.features.product_list.presentation.components.ProductListView
import react.create
import react.dom.client.createRoot
import web.dom.Element

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupProductListIsland() {
    console.log("[setupProductListIsland] Setting up product list island...")

    try {
        // Initialize Koin

        val viewModel = ProductListViewmodel()

        val rootElement = document.getElementById("product-list-island-root") as? Element
            ?: error("Root element not found")

        createRoot(rootElement).render(
            ProductListView.create {
                this.viewModel = viewModel
            }
        )

        console.log("[setupProductListIsland] ✅ Island hydrated successfully")


        window.addEventListener("beforeunload", {
            //stopKoin here
        })
    } catch (e: Exception) {
        console.error("[setupProductListIsland] Error:", e.message)
    }
}