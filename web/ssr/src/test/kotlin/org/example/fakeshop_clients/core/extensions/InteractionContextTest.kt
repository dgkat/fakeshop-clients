package org.example.fakeshop_clients.core.extensions

import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.testing.testApplication
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest
import org.example.fakeshop_clients.core.interactions.domain.InteractionHeaders
import org.example.fakeshop_clients.core.interactions.domain.InteractionSurface

/**
 * Pins the SSR half of the originating-surface rule: what an island writes into the PDP link as
 * `?src=`/`?pos=` has to come back out as the `X-Surface` / `X-Position` headers the gateway reads.
 *
 * Every failure mode here is silent — the page renders either way, the `surface` column just stops
 * carrying information — so these assertions are the only thing standing between a wiring mistake
 * and months of views that all read `PRODUCT_SCREEN`.
 */
class InteractionContextTest {

    @Test
    fun attributionFromTheQueryStringBecomesOutboundHeaders() = runTest {
        testApplication {
            application {
                routing {
                    get("/product/{id}") {
                        val headers = call.interactionContext().toHeaders()
                        call.respondText(
                            "${headers[InteractionHeaders.SURFACE]}|${headers[InteractionHeaders.POSITION]}"
                        )
                    }
                }
            }
            assertEquals(
                "SEARCH|1",
                client.get("/product/socks-123?src=SEARCH&pos=1").bodyAsText()
            )
        }
    }

    @Test
    fun anUnrecognisedSurfaceFallsBackToProductScreenAndStillRenders() = runTest {
        testApplication {
            application {
                routing {
                    get("/product/{id}") {
                        call.respondText(call.interactionContext().surface.wireValue)
                    }
                }
            }
            // An old bookmark with a stale `src` must render the page normally, not fail.
            val response = client.get("/product/socks-123?src=SOME_OLD_SURFACE&pos=notanumber")
            assertEquals(200, response.status.value)
            assertEquals(InteractionSurface.PRODUCT_SCREEN.wireValue, response.bodyAsText())
        }
    }

    @Test
    fun aRequestWithoutAttributionReportsProductScreenAndNoPosition() = runTest {
        testApplication {
            application {
                routing {
                    get("/product/{id}") {
                        val context = call.interactionContext()
                        call.respondText("${context.surface.wireValue}|${context.position}")
                    }
                }
            }
            assertEquals(
                "${InteractionSurface.PRODUCT_SCREEN.wireValue}|null",
                client.get("/product/socks-123").bodyAsText()
            )
        }
    }

    @Test
    fun aFirstVisitMintsASessionAndForwardsTheSameIdItSetsOnTheBrowser() = runTest {
        testApplication {
            application {
                routing {
                    get("/product/{id}") {
                        call.respondText(
                            call.interactionContext().toHeaders()[InteractionHeaders.SESSION_ID]
                                ?: "absent"
                        )
                    }
                }
            }
            // No cookies at all — the SSR minting path is what keeps the very first view of a new
            // visitor off the 'unknown' sentinel.
            val response = client.get("/product/socks-123")
            val forwarded = response.bodyAsText()
            val setCookies = response.headers.getAll(HttpHeaders.SetCookie).orEmpty()

            assertTrue(forwarded.isNotBlank() && forwarded != "absent")
            val minted = setCookies
                .firstOrNull { it.startsWith("${InteractionHeaders.SESSION_ID_COOKIE}=") }
            assertNotNull(minted, "a first visit must receive a session cookie")
            // Minting and forwarding must not disagree within one request.
            assertTrue(minted.contains(forwarded))
            assertTrue(
                setCookies.any { it.startsWith("${InteractionHeaders.SESSION_TOUCHED_COOKIE}=") },
                "the staleness clock must be set alongside the id"
            )
        }
    }

    @Test
    fun aProductScreenActionCarriesNoPositionAndNeverInheritsAListSurface() = runTest {
        testApplication {
            application {
                routing {
                    get("/product/like/{id}") {
                        val context = call.productScreenInteractionContext()
                        call.respondText("${context.surface.wireValue}|${context.position}")
                    }
                }
            }
            // The HTMX like endpoints are interaction call sites, but the tap happened on the PDP
            // itself — a stray `src` on the URL must not re-attribute it to a list.
            assertEquals(
                "${InteractionSurface.PRODUCT_SCREEN.wireValue}|null",
                client.get("/product/like/socks-123?src=HOME_SHELF&pos=4").bodyAsText()
            )
        }
    }
}
