package org.example.fakeshop_clients.core.network

interface UrlProvider {
    val baseUrl: String

    operator fun invoke(): String = baseUrl
}
