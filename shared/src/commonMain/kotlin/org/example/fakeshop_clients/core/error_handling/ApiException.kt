package org.example.fakeshop_clients.core.error_handling

class ApiException(
    val status: Int,
    val rawBody: String?,
    val envelope: ApiErrorEnvelope?,
    val statusDescription: String? = null
) : Exception("HTTP $status" + (statusDescription?.let { " $it" } ?: ""))
