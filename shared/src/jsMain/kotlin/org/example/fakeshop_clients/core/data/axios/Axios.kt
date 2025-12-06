package org.example.fakeshop_clients.core.data.axios

import kotlin.js.Promise

@JsModule("axios")
@JsNonModule
external val axios: Axios

external interface Axios {
    fun get(url: String, config: AxiosRequestConfig = definedExternally): Promise<AxiosResponse>
    fun post(url: String, data: Any?, config: AxiosRequestConfig = definedExternally): Promise<AxiosResponse>
    fun put(url: String, data: Any?, config: AxiosRequestConfig = definedExternally): Promise<AxiosResponse>
    fun delete(url: String, config: AxiosRequestConfig = definedExternally): Promise<AxiosResponse>

    operator fun invoke(config: AxiosRequestConfig): Promise<AxiosResponse>

    val interceptors: AxiosInterceptors
    val defaults: AxiosDefaults
}

external interface AxiosDefaults {
    var withCredentials: Boolean
}

external interface AxiosInterceptors {
    val request: AxiosInterceptorManager<AxiosRequestConfig>
    val response: AxiosInterceptorManager<AxiosResponse>
}

external interface AxiosInterceptorManager<T> {
    fun use(
        onFulfilled: (T) -> dynamic,
        onRejected: ((dynamic) -> dynamic)? = definedExternally
    ): Int

    fun eject(id: Int)
}

external interface AxiosRequestConfig {
    var url: String?
    var method: String?
    var headers: dynamic
    var data: Any?
    var _retry: Boolean?
    var withCredentials: Boolean?
}

external interface AxiosResponse {
    val data: dynamic
    val status: Int
    val statusText: String
    val config: AxiosRequestConfig
}

external interface AxiosError {
    val config: AxiosRequestConfig
    val response: AxiosResponse?
    val message: String?
}