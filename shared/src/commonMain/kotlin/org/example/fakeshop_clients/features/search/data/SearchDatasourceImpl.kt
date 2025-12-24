package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.data.ApiClient
import org.example.fakeshop_clients.core.data.get
import org.example.fakeshop_clients.features.search.data.models.SearchResponse

class SearchDatasourceImpl(private val client: ApiClient) : SearchDatasource {

    override suspend fun searchProducts(query: String): SearchResponse {
        val searchResponse = client.get<SearchResponse>(
            path = "/api/search?q=$query"
        )
        return searchResponse
    }
}
