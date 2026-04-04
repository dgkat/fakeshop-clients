package org.example.fakeshop_clients.features.recents.domain

import org.example.fakeshop_clients.core.error_handling.NetworkError
import org.example.fakeshop_clients.core.error_handling.Result
import org.example.fakeshop_clients.features.home.domain.models.BriefProduct

class RecentsServiceImpl(
    private val repository: RecentsRepository
) : RecentsService {

    override suspend fun getRecentlyViewed(): Result<List<BriefProduct>, NetworkError> {
        return repository.getRecentlyViewed()
    }
}
