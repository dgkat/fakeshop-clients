package org.example.fakeshop_clients.features.bdui.presentation

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.createHTML
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.extensions.extractCookies
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.BduiJson
import org.example.fakeshop_clients.features.bdui.data.SSRReplaceDatasource
import org.example.fakeshop_clients.features.bdui.domain.models.UiNode
import org.example.fakeshop_clients.features.bdui.presentation.render.renderBduiNode
import org.koin.ktor.ext.inject

/**
 * Server-side "client-side apply" for the Replace feature. The browser never sees
 * `replaceBindings`: it posts `productId` + a literal `targetSlotId`, and this route resolves
 * the binding → layout `node` → data `values`, then returns the rendered fragment for htmx to
 * swap in place of `#<targetSlotId>` (`hx-swap="outerHTML"`).
 *
 * **Standalone scope:** the fragment renders against the layout's own `values` — never the
 * product `bindData`. **Drift / no-binding → `204 No Content`** (no swap, never an error).
 */
fun Route.bduiReplaceRoute() {
    val replaceDatasource by inject<SSRReplaceDatasource>()

    post("/bdui/replace") {
        val params = call.receiveParameters()
        val productId = params["productId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest)
        val targetSlotId = params[BduiConstants.TARGET_SLOT_ID_KEY]
            ?: return@post call.respond(HttpStatusCode.BadRequest)

        val cookies = call.extractCookies()

        // Bindings: unknown product / no bindings both come back as 200 [] → no-op.
        val bindings = when (val res = replaceDatasource.getReplaceBindings(productId, cookies)) {
            is Result.Success -> res.data
            is Result.Error -> return@post call.respond(HttpStatusCode.NoContent)
        }
        val binding = bindings.firstOrNull { it.targetSlotId == targetSlotId }
            ?: return@post call.respond(HttpStatusCode.NoContent)

        // Layout + data in parallel. Either 404 (drift) → null → no-op.
        val (layoutRes, dataRes) = coroutineScope {
            val layoutDef = async { replaceDatasource.getReplaceLayout(binding.layoutId, cookies) }
            val dataDef = async { replaceDatasource.getReplaceData(binding.dataId, cookies) }
            layoutDef.await() to dataDef.await()
        }

        val layout = (layoutRes as? Result.Success)?.data
            ?: return@post call.respond(HttpStatusCode.NoContent)
        val data = (dataRes as? Result.Success)?.data
            ?: return@post call.respond(HttpStatusCode.NoContent)

        val node = BduiJson.decodeFromJsonElement(UiNode.serializer(), layout.node)
        val values = data.values

        // Wrap in id="<targetSlotId>" so the outerHTML swap stays self-targeting. Render the
        // replacement against `values` standalone — no product bindData merge.
        val fragment = createHTML().div {
            id = targetSlotId
            renderBduiNode(node, values, "pdp", productId)
        }
        call.respondText(fragment, ContentType.Text.Html, HttpStatusCode.OK)
    }
}
