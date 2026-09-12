package org.example.fakeshop_clients.core.data.axios

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.example.fakeshop_clients.core.auth.domain.InstallIdProvider
import org.example.fakeshop_clients.core.auth.domain.Role
import org.example.fakeshop_clients.core.auth.domain.SessionMutator
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.interactions.data.WebSessionIdProvider
import org.example.fakeshop_clients.core.interactions.domain.InteractionHeaders
import kotlin.js.Promise
import kotlin.reflect.KClass
import kotlin.reflect.KType

class AxiosClient(
    private val baseUrl: String,
    private val webAuthDatasource: WebAuthDatasource,
    private val sessionMutator: SessionMutator,
    private val installIdProvider: InstallIdProvider,
    private val sessionIdProvider: WebSessionIdProvider,
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
                // Synchronous by necessity — an axios request interceptor cannot await, which is
                // why WebSessionIdProvider keeps a non-suspend read as its primitive.
                val sessionId = try {
                    sessionIdProvider.currentSync()
                } catch (_: Throwable) {
                    null
                }
                if (!sessionId.isNullOrBlank()) {
                    if (config.headers == null) config.headers = js("{}")
                    config.headers[InteractionHeaders.SESSION_ID] = sessionId
                }
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
                            var recovered = false
                            try {
                                recovered = try {
                                    val refreshResult = webAuthDatasource.refreshSession()
                                    refreshResult is Result.Success && refreshResult.data
                                } catch (_: Throwable) {
                                    false
                                }

                                if (!recovered) {
                                    val installId = installIdProvider.get()
                                    recovered = try {
                                        val guestResult = webAuthDatasource.guest(installId)
                                        guestResult is Result.Success && guestResult.data
                                    } catch (_: Throwable) {
                                        false
                                    }
                                    if (recovered) sessionMutator.setAuthenticated(Role.GUEST)
                                }
                            } finally {
                                isRefreshing = false
                                if (recovered) onSessionRefreshed() else onSessionRefreshFailed()
                            }

                            if (recovered) {
                                retryRequest(originalRequest)
                                    .then { resolve(it) }
                                    .catch { reject(wrapAxiosError(it.asDynamic())) }
                            } else {
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
        sessionMutator.setBootstrapFailed()
        refreshSubscribers.forEach { callback -> callback(false) }
        refreshSubscribers.clear()
    }

    override suspend fun <T : Any> get(
        path: String,
        responseType: KType,
        headers: Map<String, String>
    ): T {
        val response = axios.get("$baseUrl$path", headerConfig(headers)).await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> post(
        path: String,
        body: B,
        responseType: KType,
        headers: Map<String, String>
    ): T {
        val response = axios.post("$baseUrl$path", body.toJsBody(), headerConfig(headers)).await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <T : Any, B : Any> put(
        path: String,
        body: B,
        responseType: KType,
        headers: Map<String, String>
    ): T {
        val response = axios.put("$baseUrl$path", body.toJsBody(), headerConfig(headers)).await()
        return parseResponse(response.data, responseType)
    }

    override suspend fun <T : Any> delete(
        path: String,
        responseType: KType,
        headers: Map<String, String>
    ): T {
        val response = axios.delete("$baseUrl$path", headerConfig(headers)).await()
        return parseResponse(response.data, responseType)
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <B : Any> postNoContent(
        path: String,
        body: B,
        bodyType: KClass<B>,
        headers: Map<String, String>
    ) {
        axios.post("$baseUrl$path", body.toJsBody(bodyType), headerConfig(headers)).await()
    }

    @OptIn(InternalSerializationApi::class)
    override suspend fun <B : Any> putNoContent(
        path: String,
        body: B,
        bodyType: KClass<B>,
        headers: Map<String, String>
    ) {
        axios.put("$baseUrl$path", body.toJsBody(bodyType), headerConfig(headers)).await()
    }

    override suspend fun deleteNoContent(path: String, headers: Map<String, String>) {
        axios.delete("$baseUrl$path", headerConfig(headers)).await()
    }

    private fun headerConfig(headers: Map<String, String>): AxiosRequestConfig {
        val config = js("{}").unsafeCast<AxiosRequestConfig>()
        if (headers.isNotEmpty()) {
            val headerObject = js("{}")
            headers.forEach { (key, value) -> headerObject[key] = value }
            config.headers = headerObject
        }
        return config
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private fun <B : Any> B.toJsBody(bodyType: KClass<B> = this::class as KClass<B>): dynamic {
        val bodySerializer = bodyType.serializer() as SerializationStrategy<B>
        return JSON.parse(jsonParser.encodeToString(bodySerializer, this))
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : Any> parseResponse(data: dynamic, responseType: KType): T {
        val jsonString = JSON.stringify(data)
        // serializer(KType) keeps generic arguments (e.g. List<Foo>), unlike a bare KClass.
        return jsonParser.decodeFromString(serializer(responseType), jsonString) as T
    }
}