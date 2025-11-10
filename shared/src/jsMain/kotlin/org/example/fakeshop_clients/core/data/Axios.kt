package org.example.fakeshop_clients.core.data

import kotlin.js.Promise

@JsModule("axios")
@JsNonModule
external val axios: Axios

external interface Axios {
    fun get(url: String): Promise<AxiosResponse>
    fun post(url: String, data: Any?): Promise<AxiosResponse>
    fun put(url: String, data: Any?): Promise<AxiosResponse>
    fun delete(url: String): Promise<AxiosResponse>
}

external interface AxiosResponse {
    val data: dynamic
    val status: Int
    val statusText: String
}