package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.search.domain.SearchRepository
import org.example.fakeshop_clients.features.search.domain.models.SearchResult

class SearchRepositoryImpl(private val datasource: SearchDatasource) : SearchRepository {

    override suspend fun searchByQuery(query: String): List<SearchResult> {
        val result = datasource.searchProducts(query)

        return when (result) {
            is Result.Success -> {
                result.data.products.map { product ->
                    SearchResult(
                        productId = product.id,
                        productName = product.name
                    )
                }
            }
            is Result.Error -> {
                println("Search error: ${result.error}")
                emptyList()
            }
        }
    }
}
