package org.example.fakeshop_clients.core.interactions.domain

/**
 * Web's carrier for the originating-list attribution.
 *
 * On mobile the surface rides a navigation argument; on web the tap is a full page load into SSR,
 * so it has to survive as a query parameter. The link is written by the island/SPA that owns the
 * list, and read back by the SSR route ([org.example.fakeshop_clients.core.extensions] in `web/ssr`),
 * which turns it into `X-Surface` / `X-Position` headers.
 *
 * Both sides use these constants so the two halves cannot drift. Unrecognised values degrade to
 * `PRODUCT_SCREEN` rather than failing — an old bookmark with a stale `src` must still render.
 */
object InteractionQuery {
    const val SURFACE_PARAM = "src"
    const val POSITION_PARAM = "pos"

    /**
     * The canonical locale-prefixed PDP path, with attribution attached. [position] is the 0-based
     * rank within the originating list; omit it for taps that did not come from one.
     */
    fun productDetailPath(
        locale: String,
        productId: String,
        surface: InteractionSurface = InteractionSurface.PRODUCT_SCREEN,
        position: Int? = null
    ): String = buildString {
        append("/")
        append(locale)
        append("/product/")
        append(productId)
        append("?")
        append(SURFACE_PARAM)
        append("=")
        append(surface.wireValue)
        position?.let {
            append("&")
            append(POSITION_PARAM)
            append("=")
            append(it)
        }
    }
}
