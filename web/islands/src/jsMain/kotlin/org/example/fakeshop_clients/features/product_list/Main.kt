import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.WebKoinManager
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
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

    koin.get<CoroutineScope>(AppScopeQualifier).launch {
        koin.get<SessionBootstrapper>().bootstrap()
    }

    val viewModel = koin.get<ProductListViewmodel>()

    val rootElement = document.getElementById("product-list-island-root") as? Element
        ?: error("Root element not found")

    createRoot(rootElement).render(
        ProductListView.create {
            this.viewModel = viewModel
        }
    )

    window.addEventListener("pageshow", { event ->
        if (event.asDynamic().persisted == true) {
            viewModel.refreshFavorites()
        }
    })
}