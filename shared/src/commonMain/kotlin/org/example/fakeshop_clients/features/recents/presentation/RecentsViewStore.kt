package org.example.fakeshop_clients.features.recents.presentation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.error_handling.fold
import org.example.fakeshop_clients.features.home.domain.mappers.DomainToPresentationBriefProductMapper
import org.example.fakeshop_clients.features.recents.domain.RecentsService

class RecentsViewStore(
    private val scope: CoroutineScope,
    private val recentsService: RecentsService,
    private val mapper: DomainToPresentationBriefProductMapper
) {

    private val _state = MutableStateFlow(RecentsState())
    val state: StateFlow<RecentsState> = _state.asStateFlow()

    fun onEvent(event: RecentsEvent) {
        when (event) {
            RecentsEvent.LoadRecents -> loadRecents()
            RecentsEvent.Retry -> loadRecents()
        }
    }

    private fun loadRecents() {
        _state.update { it.copy(isLoading = true, error = null) }
        scope.launch {
            recentsService.getRecentlyViewed().fold(
                onSuccess = { products ->
                    _state.update {
                        it.copy(products = mapper.map(products), isLoading = false, error = null)
                    }
                },
                onError = { networkError ->
                    _state.update {
                        it.copy(isLoading = false, error = RecentsError.Network(networkError))
                    }
                }
            )
        }
    }
}
