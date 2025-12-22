package org.example.fakeshop_clients.features.search.presentation.hooks

import kotlinx.browser.window
import react.useEffectWithCleanup
import react.useRef
import react.useState
import web.animations.requestAnimationFrame

data class ScrollState(
    val offset: Double = 0.0,
    val isScrollingDown: Boolean = false
)

/**
 * Hook to track scroll offset for scroll-reactive search bar behavior.
 * Bar snaps to either fully visible (0px) or fully hidden (-maxOffset) with smooth CSS transitions.
 *
 * @param maxOffset Maximum offset in pixels (defaults to 72.0px - search bar height)
 * @param threshold Scroll threshold in pixels before changing state (default: 5px)
 * @return ScrollState containing current offset (0 or -maxOffset) and scroll direction
 */
fun useScrollOffset(
    maxOffset: Double = 72.0,
    threshold: Double = 5.0
): ScrollState {
    var scrollState by useState(ScrollState())
    val lastScrollY = useRef(0.0)
    val ticking = useRef(false)

    useEffectWithCleanup {
        val updateScrollOffset: () -> Unit = {
            val currentScrollY = window.scrollY
            val delta = currentScrollY - (lastScrollY.current ?: 0.0)

            // Determine target state based on scroll direction and threshold
            val newOffset = when {
                // At top of page → always visible
                currentScrollY <= 0 -> 0.0

                // Scrolling down beyond threshold → hide
                delta > threshold -> -maxOffset

                // Scrolling up beyond threshold → show
                delta < -threshold -> 0.0

                // Within threshold → keep current state
                else -> scrollState.offset
            }

            // Only update if state changed
            if (newOffset != scrollState.offset) {
                scrollState = ScrollState(
                    offset = newOffset,
                    isScrollingDown = delta > 0
                )
            }

            lastScrollY.current = currentScrollY
            ticking.current = false
        }

        val handleScroll: (dynamic) -> Unit = { _ ->
            if (ticking.current != true) {
                requestAnimationFrame { updateScrollOffset() }
                ticking.current = true
            }
        }

        window.addEventListener("scroll", handleScroll)
        lastScrollY.current = window.scrollY

        onCleanup {
            window.removeEventListener("scroll", handleScroll)
        }
    }

    return scrollState
}
