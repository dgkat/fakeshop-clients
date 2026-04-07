package org.example.fakeshop_clients.features.recents.data

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.data.models.BriefProductsResponse

interface RecentsDatasource {
    suspend fun getRecentlyViewed(): Result<BriefProductsResponse, NetworkError>
}
