import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.example.fakeshop_clients.core.WebKoinManager
import org.example.fakeshop_clients.core.i18n.I18n
import org.example.fakeshop_clients.features.search.presentation.SearchViewModel
import org.example.fakeshop_clients.features.search.presentation.SearchViewStore
import org.example.fakeshop_clients.features.search.presentation.components.SearchBar
import org.example.fakeshop_clients.features.search.utils.BehaviorMapping
import org.koin.core.parameter.parametersOf
import react.create
import react.dom.client.createRoot
import web.dom.Element

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupSearchIsland() {
    console.log("[setupSearchIsland] Setting up search island...")

    try {
        // Initialize Koin if not already initialized
        WebKoinManager.initialize()

        val koin = WebKoinManager.getKoin()

        // Create scope for this island
        val scope = MainScope()

        // Get SearchViewStore with scope parameter
        val store: SearchViewStore = koin.get { parametersOf(scope) }

        // Create ViewModel
        val viewModel: SearchViewModel = koin.get { parametersOf(store) }

        // Detect page type from body data attribute
        val page = document.body?.getAttribute("data-page") ?: "home"
        val behavior = BehaviorMapping.getSearchBarBehavior(page)

        console.log("[setupSearchIsland] Page: $page, Behavior: $behavior")

        val rootElement = document.getElementById("search-island-root") as? Element
            ?: error("Search island root element not found")

        createRoot(rootElement).render(
            SearchBar.create {
                this.viewModel = viewModel
                this.behavior = behavior
                this.onNavigateToProduct = { productId ->
                    val locale = I18n.locale
                    console.log("[setupSearchIsland] Navigating to product: $productId")
                    window.location.href = "/$locale/product/$productId"
                }
            }
        )

        console.log("[setupSearchIsland] ✅ Island hydrated successfully")

        window.addEventListener("beforeunload", {
            viewModel.cleanup()
            scope.cancel()
        })
    } catch (e: Exception) {
        console.error("[setupSearchIsland] Error:", e.message)
    }
}