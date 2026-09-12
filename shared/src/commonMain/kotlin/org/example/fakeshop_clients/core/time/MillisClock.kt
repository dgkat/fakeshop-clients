package org.example.fakeshop_clients.core.time

fun interface MillisClock {
    fun nowMillis(): Long
}
