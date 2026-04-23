package org.example.fakeshop_clients

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.example.fakeshop_clients.core.auth.domain.SessionBootstrapper
import org.example.fakeshop_clients.core.auth.domain.SessionObserver
import org.example.fakeshop_clients.core.auth.domain.SessionState
import org.example.fakeshop_clients.core.navigation.components.BootstrapFailedScreen
import org.example.fakeshop_clients.core.navigation.components.MainNavigation
import org.example.fakeshop_clients.features.notifications.presentation.NotificationEventBus
import org.example.fakeshop_clients.features.notifications.presentation.PushNotificationEvent
import org.example.fakeshop_clients.ui.theme.FakeShopTheme
import org.koin.compose.koinInject

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
                    AppShell(initialProductId = notificationProductId)
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

@Composable
private fun AppShell(initialProductId: String?) {
    val sessionObserver = koinInject<SessionObserver>()
    val sessionBootstrapper = koinInject<SessionBootstrapper>()
    val scope = rememberCoroutineScope()
    val sessionState by sessionObserver.state.collectAsStateWithLifecycle()

    when (sessionState) {
        is SessionState.Authenticated -> MainNavigation(initialProductId = initialProductId)
        SessionState.BootstrapFailed -> BootstrapFailedScreen(
            onRetry = { scope.launch { sessionBootstrapper.bootstrap() } }
        )
        SessionState.Unknown -> Unit
    }
}