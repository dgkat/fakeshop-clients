package org.example.fakeshop_clients.core.data.axios

import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.error_handling.Result
import kotlin.js.Promise
import kotlin.reflect.KClass

class AxiosClient(
    private val baseUrl: String,
    private val webAuthDatasource: WebAuthDatasource
) : ApiClient {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var isRefreshing = false
    private var refreshSubscribers: MutableList<(Throwable?) -> Unit> = mutableListOf()

    private val scope = MainScope()

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
                        return@use Promise { resolve, reject ->
                            refreshSubscribers.add { refreshError ->
                                if (refreshError != null) {
                                    reject(refreshError)
                                } else {
                                    retryRequest(originalRequest)
                                        .then { resolve(it) }
                                        .catch { reject(it) }
                                }
                            }
                        }
                    }

                    originalRequest._retry = true
                    isRefreshing = true

                    return@use scope.promise {
                        try {
                            //TODO clean up try/catch ( already in .refreshSession) and else + Result.Error (duplicate)
                            val refreshResult = webAuthDatasource.refreshSession()

                            when (refreshResult) {
                                is Result.Success -> {
                                    if (refreshResult.data) {
                                        onSessionRefreshed()
                                        retryRequest(originalRequest).await()
                                    } else {
                                        val error = Exception("Session refresh returned false")
                                        onSessionRefreshFailed(error)
                                        throw error
                                    }
                                }
                                is Result.Error -> {
                                    val error = Exception("Session refresh failed: ${refreshResult.error}")
                                    onSessionRefreshFailed(error)
                                    throw error
                                }
                            }
                        } catch (refreshError: Throwable) {
                            onSessionRefreshFailed(refreshError)
                            throw refreshError
                        } finally {
                            isRefreshing = false
                        }
                    }
                }

                Promise.reject(error)
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

    private fun onSessionRefreshed() {
        refreshSubscribers.forEach { callback -> callback(null) }
        refreshSubscribers.clear()
    }

    private fun onSessionRefreshFailed(error: Throwable) {
        refreshSubscribers.forEach { callback -> callback(error) }
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

    override suspend fun deleteNoContent(path: String) {
        axios.delete("$baseUrl$path").await()
    }

    @OptIn(InternalSerializationApi::class)
    private fun <T : Any> parseResponse(data: dynamic, responseType: KClass<T>): T {
        val jsonString = JSON.stringify(data)
        return jsonParser.decodeFromString(responseType.serializer(), jsonString)
    }
}