package org.example.fakeshop_clients.core.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import org.example.fakeshop_clients.features.home.domain.TestResponse

class KtorClient(val http: HttpClient) : ApiClient {
    override suspend fun testApiCalls() {
        val response =  http.get("/objects/ff8081819782e69e019a66263f2e0f56")
        val obj = response.body<TestResponse>()
        println("android get  -> $obj")
    }
}