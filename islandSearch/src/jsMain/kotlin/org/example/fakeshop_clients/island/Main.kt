import kotlinx.browser.document
import kotlinx.browser.window
import react.FC
import react.Props
import react.create
import react.dom.client.createRoot
import react.dom.html.ReactHTML.button
import web.dom.Element

// Define your component props
external interface SearchButtonProps : Props {
    var onSearch: ((String) -> Unit)?
}

val SearchButton = FC<SearchButtonProps> { props ->
    button {
        onClick = {
            console.log("[SearchButton] Clicked!")
            props.onSearch?.invoke("search")
        }
        +"Search"
    }
}

@OptIn(ExperimentalJsExport::class)
@JsExport
fun setupIslandModule() {
    console.log("[setupIslandModule] Setting up search island...")

    try {
        // Initialize Koin

        val rootElement = document.getElementById("search-island-root") as? Element
            ?: error("Root element not found")

        createRoot(rootElement).render(
            SearchButton.create {
                onSearch = { query ->
                    console.log("[SearchButton] Search query:", query)
                }
            }
        )

        console.log("[setupIslandModule] ✅ Island hydrated successfully")


        window.addEventListener("beforeunload", {
            //stopKoin here
        })
    } catch (e: Exception) {
        console.error("[setupIslandModule] Error:", e.message)
    }
}