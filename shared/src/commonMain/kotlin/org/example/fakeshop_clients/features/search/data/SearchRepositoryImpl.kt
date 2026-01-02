package org.example.fakeshop_clients.features.search.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.core.error_handling.map
import org.example.fakeshop_clients.features.search.domain.SearchRepository
import org.example.fakeshop_clients.features.search.domain.models.SearchResult

class SearchRepositoryImpl(private val datasource: SearchDatasource) : SearchRepository {

    override suspend fun searchByQuery(query: String): Result<List<SearchResult>, NetworkError> {
        return datasource.searchProducts(query).map { response ->
            response.products.map { product ->
                SearchResult(
                    productId = product.id,
                    productName = product.name
                )
            }
        }
    }
}
