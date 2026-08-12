package org.example.fakeshop_clients.features.bdui.presentation

import io.ktor.http.HttpStatusCode
import io.ktor.server.html.respondHtml
import io.ktor.server.request.receiveParameters
import io.ktor.server.response.header
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import kotlinx.html.body
import kotlinx.html.span
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.extensions.extractCookies
import org.example.fakeshop_clients.features.bdui.BduiConstants
import org.example.fakeshop_clients.features.bdui.BduiJson
import org.example.fakeshop_clients.features.bdui.data.SSRBduiActionDatasource
import org.example.fakeshop_clients.features.bdui.domain.models.BduiActionRequest
import org.example.fakeshop_clients.features.bdui.domain.models.BduiMutation
import org.koin.ktor.ext.inject

fun Route.bduiActionRoute() {
    val actionDatasource by inject<SSRBduiActionDatasource>()

    post("/bdui/action") {
        val params = call.receiveParameters()
        val actionId = params["actionId"]
            ?: return@post call.respond(HttpStatusCode.BadRequest)
        val screen = params["screen"]
            ?: return@post call.respond(HttpStatusCode.BadRequest)
        val schemaVersion = params["schemaVersion"]?.toIntOrNull()
            ?: BduiConstants.CURRENT_SCHEMA_VERSION

        val knownKeys = setOf("actionId", "screen", "schemaVersion")
        val context: JsonObject = buildJsonObject {
            params.entries()
                .filter { it.key !in knownKeys }
                .forEach { entry -> put(entry.key, entry.value.firstOrNull() ?: "") }
        }

        val request = BduiActionRequest(
            schemaVersion = schemaVersion,
            actionId = actionId,
            screen = screen,
            context = context
        )

        val cookies = call.extractCookies()

        when (val result = actionDatasource.postAction(request, cookies)) {
            is Result.Error -> call.respond(HttpStatusCode.InternalServerError)
            is Result.Success -> {
                val mutations = result.data.mutations
                if (mutations.isEmpty()) {
                    call.respond(HttpStatusCode.NoContent)
                    return@post
                }

                when (val mutation = mutations.first()) {
                    is BduiMutation.UpdateBindData -> {
                        val patchJson = BduiJson.encodeToString(JsonObject.serializer(), mutation.patch)
                        call.response.header("HX-Trigger", """{"bdui:selectionUpdate":$patchJson}""")
                        call.respond(HttpStatusCode.NoContent)
                    }

                    is BduiMutation.ShowToast -> {
                        call.response.header("HX-Retarget", "#bdui-toast-region")
                        call.response.header("HX-Reswap", "innerHTML")
                        call.respondHtml(HttpStatusCode.OK) {
                            body {
                                span(classes = "bdui-toast bdui-toast-${mutation.severity.name}") {
                                    +mutation.message
                                }
                            }
                        }
                    }

                    is BduiMutation.Navigate -> {
                        call.response.header("HX-Redirect", mutation.url)
                        call.respond(HttpStatusCode.OK)
                    }

                    is BduiMutation.ReplaceSlot -> {
                        // Not implemented for web SSR in this phase
                        call.respond(HttpStatusCode.NoContent)
                    }
                }
            }
        }
    }
}
