package org.example.fakeshop_clients.core.data.axios

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.error_handling.Result
import kotlin.js.Promise
import kotlin.reflect.KClass

class AxiosClient(
    private val baseUrl: String,
    private val webAuthDatasource: WebAuthDatasource,
    private val sessionMutator: SessionMutator,
    private val scope: CoroutineScope
) : ApiClient {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var isRefreshing = false
    private var refreshSubscribers: MutableList<(Boolean) -> Unit> = mutableListOf()

    init {
        axios.defaults.withCredentials = true
        setupInterceptors()
    }

    private fun setupInterceptors() {
        axios.interceptors.request.use(
            onFulfilled = { config ->
                config.withCredentials = true
                config
            },
            onRejected = { error ->
                Promise.reject(error)
            }
        )

        axios.interceptors.response.use(
            onFulfilled = { response ->
                response
            },
            onRejected = { error ->
                val originalRequest = error.config
                val status = error.response?.status

                if (status == 401 && originalRequest._retry != true) {
                    if (isRefreshing) {
                        return@use Promise<AxiosResponse> { resolve, reject ->
                            refreshSubscribers.add { success ->
                                if (success) {
                                    retryRequest(originalRequest)
                                        .then { resolve(it) }
                                        .catch { reject(wrapAxiosError(it.asDynamic())) }
                                } else {
                                    reject(wrapAxiosError(error))
                                }
                            }
                        }
                    }

                    originalRequest._retry = true
                    isRefreshing = true

                    return@use Promise<AxiosResponse> { resolve, reject ->
                        scope.promise {
                            val refreshSucceeded = try {
                                val refreshResult = webAuthDatasource.refreshSession()
                                refreshResult is Result.Success && refreshResult.data
                            } catch (_: Throwable) {
                                false
                            } finally {
                                isRefreshing = false
                            }

                            if (refreshSucceeded) {
                                onSessionRefreshed()
                                retryRequest(originalRequest)
                                    .then { resolve(it) }
                                    .catch { reject(wrapAxiosError(it.asDynamic())) }
                            } else {
                                onSessionRefreshFailed()
                                reject(wrapAxiosError(error))
                            }
                        }
                    }
                }

                Promise.reject(wrapAxiosError(error))
            }
        )
    }

    private fun retryRequest(config: AxiosRequestConfig): Promise<AxiosResponse> {
        return when (config.method?.lowercase()) {
            "get" -> axios.get(config.url ?: "", config)
            "post" -> axios.post(config.url ?: "", config.data, config)
            "put" -> axios.put(config.url ?: "", config.data, config)
            "delete" -> axios.delete(config.url ?: "", config)
            else -> Promise.reject(js("new Error('Unsupported method')"))
        }
    }

    private fun wrapAxiosError(error: dynamic): Exception {
        val message = error?.message?.toString() ?: "Request failed"
        val wrapped = Exception(message)
        wrapped.asDynamic().response = error?.response
        wrapped.asDynamic().code = error?.code
        return wrapped
    }

    private fun onSessionRefreshed() {
        refreshSubscribers.forEach { callback -> callback(true) }
        refreshSubscribers.clear()
    }

    private fun onSessionRefreshFailed() {
        sessionMutator.setLoggedOut()
        refreshSubscribers.forEach { callback -> callback(false) }
        refreshSubscribers.clear()
    }

    override suspend fun <T : Any> get(path: String, responseType: KClass<T>): T {
        val response = axios.get("$baseUrl$path").await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = body::class.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        val bodyObject = JSON.parse<dynamic>(bodyJson)

        val response = axios.post("$baseUrl$path", bodyObject).await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> put(
        path: String,
        body: B,
        responseType: KClass<T>
    ): T {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = body::class.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        val bodyObject = JSON.parse<dynamic>(bodyJson)

        val response = axios.put("$baseUrl$path", bodyObject).await()
        return parseResponse(response.data, responseType)
    }

    override suspend fun <T : Any> delete(path: String, responseType: KClass<T>): T {
        val response = axios.delete("$baseUrl$path").await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <B : Any> postNoContent(path: String, body: B, bodyType: KClass<B>) {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = bodyType.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        val bodyObject = JSON.parse<dynamic>(bodyJson)
        axios.post("$baseUrl$path", bodyObject).await()
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <B : Any> putNoContent(path: String, body: B, bodyType: KClass<B>) {
        @Suppress("UNCHECKED_CAST")
        val bodySerializer = bodyType.serializer() as SerializationStrategy<B>
        val bodyJson = jsonParser.encodeToString(bodySerializer, body)
        val bodyObject = JSON.parse<dynamic>(bodyJson)
        axios.put("$baseUrl$path", bodyObject).await()
    }

    override suspend fun deleteNoContent(path: String) {
        axios.delete("$baseUrl$path").await()
    }

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> parseResponse(data: dynamic, responseType: KClass<T>): T {
        val jsonString = JSON.stringify(data)
        return jsonParser.decodeFromString(responseType.serializer(), jsonString)
    }
}