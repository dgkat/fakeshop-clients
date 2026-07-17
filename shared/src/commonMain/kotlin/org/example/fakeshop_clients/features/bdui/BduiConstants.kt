package org.example.fakeshop_clients.features.bdui

object BduiConstants {
    const val CURRENT_SCHEMA_VERSION = 2

    /** Client-side-only action: swaps a slot's subtree. Never POSTed to `/ui/action`. */
    const val REPLACE_ACTION_ID = "replace"

    /** Context key carrying the literal target slot id for a [REPLACE_ACTION_ID] action. */
    const val TARGET_SLOT_ID_KEY = "targetSlotId"

    /** Client-side-only action: navigates to an internal route. Never POSTed to `/ui/action`. */
    const val NAVIGATE_ACTION_ID = "navigate"

    /** Context key carrying the literal destination path for a [NAVIGATE_ACTION_ID] action. */
    const val URL_KEY = "url"

    /** Context key carrying the literal `"true"` flag to replace the current back-stack entry. */
    const val REPLACE_STACK_KEY = "replace"

    /**
     * Context keys whose values are literals, passed through verbatim — everything else in
     * `contextBindings` is a bind path resolved against bindData.
     */
    val LITERAL_CONTEXT_KEYS = setOf(TARGET_SLOT_ID_KEY, URL_KEY, REPLACE_STACK_KEY)
}
