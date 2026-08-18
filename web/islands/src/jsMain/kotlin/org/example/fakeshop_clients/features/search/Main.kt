import org.example.fakeshop_clients.core.interactions.domain.InteractionQuery
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.WebKoinManager
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.concurrency.AppScopeQualifier
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

    koin.get<CoroutineScope>(AppScopeQualifier).launch {
        koin.get<SessionBootstrapper>().bootstrap()
    }

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
            this.onNavigateToProduct = { productId, position ->
                window.location.href = InteractionQuery.productDetailPath(
                    locale = I18n.locale,
                    productId = productId,
                    surface = InteractionSurface.SEARCH,
                    position = position
                )
            }
        }
    )

    window.addEventListener("pagehide", { event ->
        if (event.asDynamic().persisted != true) {
            viewModel.cleanup()
            scope.cancel()
        }
    })
}