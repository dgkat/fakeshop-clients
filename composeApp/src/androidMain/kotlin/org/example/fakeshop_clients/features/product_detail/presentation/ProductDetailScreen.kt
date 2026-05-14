package org.example.fakeshop_clients.features.product_detail.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.example.fakeshop_clients.features.productDetail.presentation.BriefProductState
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailEffect
import org.example.fakeshop_clients.features.productDetail.presentation.ProductDetailEvent
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.LocalBduiActionHandler
import org.example.fakeshop_clients.features.product_detail.presentation.bdui.rememberBduiActionHandler
import org.example.fakeshop_clients.features.product_detail.presentation.components.ErrorContent
import org.example.fakeshop_clients.features.product_detail.presentation.components.LoadingContent
import org.example.fakeshop_clients.features.product_detail.presentation.components.ProductContent
import org.example.fakeshop_clients.features.search_bar.presentation.components.rememberSearchBarScrollState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProductDetailScreen(
    productId: String,
    contentPadding: PaddingValues,
    onScrollOffsetChange: (Float) -> Unit,
    onNavigate: (url: String, replace: Boolean) -> Unit = { _, _ -> },
    viewModel: ProductDetailViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val appContext = LocalContext.current
    val scrollState = rememberSearchBarScrollState(onScrollOffsetChange = onScrollOffsetChange)

    LaunchedEffect(productId) {
        viewModel.onEvent(ProductDetailEvent.LoadProduct(productId))
    }

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                is ProductDetailEffect.ShowToast ->
                    Toast.makeText(appContext, effect.message, Toast.LENGTH_SHORT).show()
                is ProductDetailEffect.Navigate ->
                    onNavigate(effect.url, effect.replace)
            }
        }
    }

    when (val briefState = state.briefState) {
        is BriefProductState.Loading -> {
            LoadingContent(modifier = Modifier.padding(contentPadding))
        }

        is BriefProductState.Error -> {
            ErrorContent(
                error = briefState.error,
                onRetry = { viewModel.onEvent(ProductDetailEvent.Retry) },
                modifier = Modifier.padding(contentPadding)
            )
        }

        is BriefProductState.Success -> {
            val actionHandler = rememberBduiActionHandler(viewModel)
            CompositionLocalProvider(LocalBduiActionHandler provides actionHandler) {
                ProductContent(
                    briefProduct = briefState.product,
                    galleryUrls = state.galleryUrls,
                    bduiBodyState = state.bduiBodyState,
                    isFavorited = state.isFavorited,
                    isFavoriteLoading = state.isFavoriteLoading,
                    onToggleFavorite = { viewModel.onEvent(ProductDetailEvent.ToggleFavorite) },
                    scrollState = scrollState,
                    contentPadding = contentPadding,
                    modifier = Modifier
                )
            }
        }
    }
}
