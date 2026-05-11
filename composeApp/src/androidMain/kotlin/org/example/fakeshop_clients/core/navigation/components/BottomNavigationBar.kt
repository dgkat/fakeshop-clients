package org.example.fakeshop_clients.core.navigation.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import org.example.fakeshop_clients.core.navigation.BottomNavItem
import org.jetbrains.compose.resources.stringResource

@Composable
fun BottomNavigationBar(
    activeTab: String?,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .graphicsLayer(clip = false)
            .drawWithContent {
                drawContent()
                val shadowHeight = 8.dp.toPx()
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.12f)),
                        startY = -shadowHeight,
                        endY = 0f
                    ),
                    topLeft = Offset(0f, -shadowHeight),
                    size = Size(size.width, shadowHeight)
                )
            },
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 3.dp
    ) {
        BottomNavItem.entries.forEach { item ->
            val isSelected = activeTab == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.icon,
                        contentDescription = stringResource(item.titleRes)
                    )
                },
                label = {
                    Text(
                        text = stringResource(item.titleRes),
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                alwaysShowLabel = true
            )
        }
    }
}
