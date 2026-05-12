package org.example.fakeshop_clients.features.product_detail.presentation.bdui

import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import org.example.fakeshop_clients.features.bdui.domain.models.ActionRef

// v1: log + Toast. Full action protocol (BE-driven actionId → response → patch UI) is a future TODO.
@Composable
fun rememberBduiActionHandler(): (ActionRef) -> Unit {
    val context = LocalContext.current
    return remember(context) {
        { action ->
            Log.d("BduiAction", "type=${action.type} target=${action.target}")
            val label = action.target?.let { "${action.type}: $it" } ?: action.type
            Toast.makeText(context, label, Toast.LENGTH_SHORT).show()
        }
    }
}
