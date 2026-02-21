import kotlinx.browser.document
import kotlinx.browser.window
import org.example.fakeshop_clients.core.WebKoinManager
import org.example.fakeshop_clients.features.product_list.presentation.ProductListViewmodel
import org.example.fakeshop_clients.features.product_list.presentation.components.ProductListView
import react.create
import react.dom.client.createRoot
import web.dom.Element

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupProductListIsland() {
    WebKoinManager.initialize()

    val koin = WebKoinManager.getKoin()

    val viewModel = koin.get<ProductListViewmodel>()

    val rootElement = document.getElementById("product-list-island-root") as? Element
        ?: error("Root element not found")

    createRoot(rootElement).render(
        ProductListView.create {
            this.viewModel = viewModel
        }
    )
}