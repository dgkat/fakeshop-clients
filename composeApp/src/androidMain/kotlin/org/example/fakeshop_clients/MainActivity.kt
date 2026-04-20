package org.example.fakeshop_clients

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.navigation.components.MainNavigation
import org.example.fakeshop_clients.features.notifications.presentation.NotificationEventBus
import org.example.fakeshop_clients.features.notifications.presentation.PushNotificationEvent
import org.example.fakeshop_clients.ui.theme.FakeShopTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val notificationProductId = extractProductId(intent)

        setContent {
            FakeShopTheme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    MainNavigation(initialProductId = notificationProductId)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        val productId = extractProductId(intent) ?: return
        CoroutineScope(Dispatchers.Main).launch {
            NotificationEventBus.emit(PushNotificationEvent.OpenProduct(productId))
        }
    }

    private fun extractProductId(intent: Intent?): String? {
        val extras = intent?.extras ?: return null
        if (extras.getString("type") != "price_drop") return null
        return extras.getString("productId")
    }
}