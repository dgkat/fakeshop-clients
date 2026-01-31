package org.example.fakeshop_clients.features.productDetailPage.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders
import io.ktor.util.reflect.TypeInfo
import org.example.fakeshop_clients.core.data.KtorNetworkExceptionMapper
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.core.models.Cookies
import org.example.fakeshop_clients.features.home.data.models.BriefProductResponse
import org.example.fakeshop_clients.features.productDetail.data.models.DetailedProductResponse

class SSRProductDetailDatasourceImpl(
    private val httpClient: HttpClient,
) : ProductDetailDatasource {
    //TODO create Safe client
    private val exceptionMapper = KtorNetworkExceptionMapper()

    override suspend fun getBriefProductById(
        id: String,
        cookies: Cookies
    ): Result<BriefProductResponse, NetworkError> {
        return try {
            val response = httpClient.get("/api/products/brief/$id") {
                val accessToken = cookies.data["accessToken"]
                if (accessToken != null) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                // Re-forward all cookies
                cookies.data.forEach { (name, value) ->
                    cookie(name, value)
                }
            }
            Result.Success(response.body(TypeInfo(BriefProductResponse::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    override suspend fun getDetailedProductById(
        id: String,
        cookies: Cookies
    ): Result<DetailedProductResponse, NetworkError> {
        return try {
            val response = httpClient.get("/api/products/detailed/$id") {
                val accessToken = cookies.data["accessToken"]
                if (accessToken != null) {
                    headers {
                        append(HttpHeaders.Authorization, "Bearer $accessToken")
                    }
                }

                cookies.data.forEach { (name, value) ->
                    cookie(name, value)
                }
            }
            Result.Success(response.body(TypeInfo(DetailedProductResponse::class)))
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

    override suspend fun toggleLike(
        productId: String,
        cookies: Cookies
    ): Result<Unit, NetworkError> {
        return try {
            val response = httpClient.post("/api/products/like/$productId") {
                val accessToken = cookies.data["accessToken"]
                headers {
                    append(HttpHeaders.Authorization, "Bearer $accessToken")
                }

                cookies.data.forEach { (name, value) ->
                    cookie(name, value)
                }
            }
            Result.Success(response.body())
        } catch (e: Exception) {
            Result.Error(exceptionMapper.map(e))
        }
    }

}