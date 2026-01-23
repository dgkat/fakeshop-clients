package org.example.fakeshop_clients.features.home.domain

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.data.ProductListDatasource
import org.example.fakeshop_clients.features.home.data.mappers.RemoteBriefProductMapper
import org.example.fakeshop_clients.features.home.presentation.productList.CategoryRow

class ProductListServiceImpl(
    private val datasource: ProductListDatasource,
    private val mapper: RemoteBriefProductMapper
) : ProductListService {

    private val categories = listOf("Electronics", "Clothing", "Books", "Home & Garden", "Sports")

    override fun getProducts(): Flow<Result<CategoryRow, NetworkError>> = channelFlow {
        val jobs = categories.map { category ->
            launch {
                when (val result = datasource.getProductsByCategory(category, limit = 5)) {
                    is Result.Success -> {
                        val products = mapper.map(result.data.briefProducts)
                        send(Result.Success(CategoryRow(category = category, products = products)))
                    }
                    is Result.Error -> {
                        send(Result.Error(result.error))
                    }
                }
            }
        }
        jobs.joinAll()
    }
}