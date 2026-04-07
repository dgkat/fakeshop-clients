package org.example.fakeshop_clients.core.notifications

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionManager
import org.example.fakeshop_clients.features.notifications.domain.NotificationPermissionStatus

class AndroidNotificationPermissionManager(
    private val context: Context
) : NotificationPermissionManager {

    override fun getPermissionStatus(): NotificationPermissionStatus {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return NotificationPermissionStatus.GRANTED
        }

        return when (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)) {
            PackageManager.PERMISSION_GRANTED -> NotificationPermissionStatus.GRANTED
            else -> {
                val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
                if (prefs.getBoolean("permission_requested", false)) {
                    NotificationPermissionStatus.DENIED
                } else {
                    NotificationPermissionStatus.NOT_DETERMINED
                }
            }
        }
    }

    override suspend fun requestPermission(): NotificationPermissionStatus {
        return getPermissionStatus()
    }

    fun markPermissionRequested() {
        val prefs = context.getSharedPreferences("notification_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("permission_requested", true).apply()
    }
}
