package org.example.fakeshop_clients.features.bdui

object BduiConstants {
    const val CURRENT_SCHEMA_VERSION = 2

    /** Client-side-only action: swaps a slot's subtree. Never POSTed to `/ui/action`. */
    const val REPLACE_ACTION_ID = "replace"

    /** Context key carrying the literal target slot id for a [REPLACE_ACTION_ID] action. */
    const val TARGET_SLOT_ID_KEY = "targetSlotId"
}
