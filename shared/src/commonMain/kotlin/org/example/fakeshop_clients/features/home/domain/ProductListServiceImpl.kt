package org.example.fakeshop_clients.features.home.domain

import kotlinx.coroutines.delay
import org.example.fakeshop_clients.core.auth.data.AuthDatasource
import org.example.fakeshop_clients.core.auth.data.AuthTokenProvider
import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl(
    private val apiClient: ApiClient,
    private val authDatasource: AuthDatasource
) : ProductListService {
    override fun getProducts(): List<CategoryRow> {
        return emptyList()
    }

    override suspend fun testApiCalls() {
        /*val tokens = authDatasource.login(username = "admin@mail.com", password = "admin")
        AuthTokenProvider.refreshToken = tokens.refreshToken
        AuthTokenProvider.accessToken = tokens.accessToken
*/
        println("accessToken: ${AuthTokenProvider.accessToken}")
        println("refreshToken: ${AuthTokenProvider.refreshToken}")
        val products = apiClient.get<BriefProductsResponse>("/api/admin/products")

        println("products -> ${products.briefProducts}")

        delay(120 * 1000)


        val products2 = apiClient.get<BriefProductsResponse>("/api/admin/products")
        println("accessToken2: ${AuthTokenProvider.accessToken}")
        println("refreshToken2: ${AuthTokenProvider.refreshToken}")
        println("products2 -> ${products2.briefProducts}")

        delay(120 * 1000)


        val products3 = apiClient.get<BriefProductsResponse>("/api/admin/products")
        println("accessToken3: ${AuthTokenProvider.accessToken}")
        println("refreshToken3: ${AuthTokenProvider.refreshToken}")
        println("products3 -> $products3")

        /*        try {
                    // Test GET request
                    println("=== Testing GET Request ===")
                    val getResponse = apiClient.get<TestResponse>("/objects/7")
                    println("Retrieved object: $getResponse")
                    println()

                    delay(1000)
                    // Test POST request
                    println("=== Testing POST Request ===")
                    val createRequest = CreateObjectRequest(
                        name = "Apple MacBook Pro 169999",
                        data = Data(
                            cpuModel = "Intel Core i9",
                            diskSize = "1 TB",
                            price = 1849.99,
                            year = 2019
                        )
                    )

                    val postResponse = apiClient.post<CreateObjectResponse, CreateObjectRequest>(
                        path = "/objects",
                        body = createRequest
                    )
                    println("Created object: $postResponse")

                    // Test PUT request (updating the object we just created)
                    println("=== Testing PUT Request ===")
                    val updateRequest = CreateObjectRequest(
                        name = "Apple MacBook Pro 16 (Updated)",
                        data = Data(
                            cpuModel = "Intel Core i9",
                            diskSize = "2 TB",
                            price = 2349.99,
                            year = 2020
                        )
                    )
                    val putResponse = apiClient.put<TestResponse, CreateObjectRequest>(
                        path = "/objects/${postResponse.id}",
                        body = updateRequest
                    )
                    println("PUT Response:")
                    println("  ID: ${putResponse.id}")
                    println("  Name: ${putResponse.name}")
                    println("  Updated Price: ${putResponse.data.price}")
                    println("  Updated Disk: ${putResponse.data.diskSize}")
                    println()

                    // Test DELETE request
                    println("=== Testing DELETE Request ===")
                    val deleteResponse = apiClient.delete<DeleteResponse>("/objects/${postResponse.id}")
                    println("DELETE Response:")
                    println("  Message: ${deleteResponse.message}")


                } catch (e: Exception) {
                    println("Error during API calls: ${e.message}")
                    e.printStackTrace()
                }*/
    }
}