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
    WebKoinManager.initialize()

    val koin = WebKoinManager.getKoin()

    val scope = MainScope()

    val store: SearchViewStore = koin.get { parametersOf(scope) }
    val viewModel: SearchViewModel = koin.get { parametersOf(store) }

    val page = document.body?.getAttribute("data-page") ?: "home"
    val behavior = BehaviorMapping.getSearchBarBehavior(page)

    val rootElement = document.getElementById("search-island-root") as? Element
        ?: error("Search island root element not found")

    createRoot(rootElement).render(
        SearchBar.create {
            this.viewModel = viewModel
            this.behavior = behavior
            this.onNavigateToProduct = { productId ->
                val locale = I18n.locale
                window.location.href = "/$locale/product/$productId"
            }
        }
    )

    window.addEventListener("beforeunload", {
        viewModel.cleanup()
        scope.cancel()
    })
}