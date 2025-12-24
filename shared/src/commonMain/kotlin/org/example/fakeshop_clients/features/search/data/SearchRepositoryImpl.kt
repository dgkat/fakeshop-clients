package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.features.search.domain.SearchRepository
import org.example.fakeshop_clients.features.search.domain.models.SearchResult

class SearchRepositoryImpl(private val datasource: SearchDatasource) : SearchRepository {

    override suspend fun searchByQuery(query: String): List<SearchResult> {
        val response = datasource.searchProducts(query)
        return response.products.map { product ->
            SearchResult(
                productId = product.id,
                productName = product.name
            )
        }
    }
}
