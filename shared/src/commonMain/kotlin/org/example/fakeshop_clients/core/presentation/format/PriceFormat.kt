package org.example.fakeshop_clients.core.presentation.format

import kotlin.math.abs
import kotlin.math.roundToLong

fun formatPrice(price: Double): String {
    val cents = abs((price * 100).roundToLong())
    val sign = if (price < 0) "-" else ""
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    return $$"$$sign$$$whole.$$frac"
}
