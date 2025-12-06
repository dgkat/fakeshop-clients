import kotlinx.coroutines.MainScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.WebAuthDatasource
import org.example.fakeshop_clients.core.data.axios.AxiosRequestConfig
import org.example.fakeshop_clients.core.data.axios.AxiosResponse
import org.example.fakeshop_clients.core.data.axios.axios
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
    private var refreshSubscribers: MutableList<(String) -> Unit> = mutableListOf()

    private val scope = MainScope()

    init {
        axios.defaults.withCredentials = true
        setupInterceptors()
    }

    private fun setupInterceptors() {
        axios.interceptors.request.use(
            onFulfilled = { config ->
                console.log("Request:", config.method, config.url)
                config.withCredentials = true
                config
            },
            onRejected = { error ->
                console.error("Request error:", error)
                Promise.reject(error)
            }
        )

        axios.interceptors.response.use(
            onFulfilled = { response ->
                console.log("Response:", response.status, response.config.url)
                response
            },
            onRejected = { error ->
                val originalRequest = error.config
                val status = error.response?.status

                if (status == 401 && originalRequest._retry != true) {
                    if (isRefreshing) {
                        console.log("Token refresh in progress, queuing request...")

                        return@use Promise { resolve, reject ->
                            refreshSubscribers.add { _ ->
                                retryRequest(originalRequest)
                                    .then { resolve(it) }
                                    .catch { reject(it) }
                            }
                        }
                    }

                    originalRequest._retry = true
                    isRefreshing = true

                    console.log("Token expired, attempting refresh...")

                    return@use scope.promise {
                        try {
                            webAuthDatasource.refreshToken()

                            console.log("Token refresh successful")

                            onTokenRefreshed("")

                            retryRequest(originalRequest).await()
                        } catch (refreshError: Throwable) {
                            console.error("Token refresh failed:", refreshError)
                            onTokenRefreshFailed()
                            throw refreshError
                        } finally {
                            isRefreshing = false
                        }
                    }
                }

                console.error("Response error:", status, error.message)
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

    private fun onTokenRefreshed(token: String) {
        refreshSubscribers.forEach { callback -> callback(token) }
        refreshSubscribers.clear()
    }

    private fun onTokenRefreshFailed() {
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
    private fun <T : Any> parseResponse(data: dynamic, responseType: KClass<T>): T {
        val jsonString = JSON.stringify(data)
        return jsonParser.decodeFromString(responseType.serializer(), jsonString)
    }
}