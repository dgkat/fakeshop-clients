package org.example.fakeshop_clients.core.interactions.domain

data class InteractionContext(
    val sessionId: String? = null,
    val surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
    val position: Int? = null
) {
    fun toHeaders(): Map<String, String> = buildMap {
        sessionId?.takeIf { it.isNotBlank() }?.let { put(InteractionHeaders.SESSION_ID, it) }
        put(InteractionHeaders.SURFACE, surface.wireValue)
        position?.let { put(InteractionHeaders.POSITION, it.toString()) }
    }

    companion object {
        val None = InteractionContext()
    }
}
