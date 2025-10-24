package org.example.fakeshop_clients.island.presentation.components

import SearchState
import SearchViewModel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.useEffectWithCleanup
import react.useState

external interface SearchButtonProps : Props {
    var viewModel: SearchViewModel?
}

val SearchButton = FC<SearchButtonProps> { props ->
    var searchState by useState(SearchState())

    useEffectWithCleanup(props.viewModel) {
        if (props.viewModel != null) {
            val scope = MainScope()
            val job = props.viewModel!!.uiState
                .onEach { newState ->
                    searchState = newState
                }
                .launchIn(scope)

            onCleanup { job::cancel }
        } else {
            null
        }
    }

    button {
        onClick = {
            console.log("[SearchButton] Clicked!")
            props.viewModel?.onSearchClick()
        }
        +"Search - Term: ${searchState.searchTerm} (${searchState.searchResults.size} results)"
    }
}