package org.example.fakeshop_clients.features.bdui.domain.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
enum class ToastSeverity {
    @SerialName("info") info,
    @SerialName("success") success,
    @SerialName("warning") warning,
    @SerialName("error") error
}

@Serializable
sealed interface BduiMutation {

    @Serializable
    @SerialName("Navigate")
    data class Navigate(
        val url: String,
        val replace: Boolean = false
    ) : BduiMutation

    @Serializable
    @SerialName("ShowToast")
    data class ShowToast(
        val message: String,
        val severity: ToastSeverity = ToastSeverity.info
    ) : BduiMutation

    @Serializable
    @SerialName("ReplaceSlot")
    data class ReplaceSlot(
        val slotId: String,
        val node: UiNode
    ) : BduiMutation

    @Serializable
    @SerialName("UpdateBindData")
    data class UpdateBindData(
        val patch: JsonObject
    ) : BduiMutation
}

@Serializable
data class BduiActionRequest(
    val schemaVersion: Int,
    val actionId: String,
    val screen: String,
    val templateId: String? = null,
    val context: JsonObject = JsonObject(emptyMap())
)

@Serializable
data class BduiActionResponse(
    val mutations: List<BduiMutation> = emptyList()
)
