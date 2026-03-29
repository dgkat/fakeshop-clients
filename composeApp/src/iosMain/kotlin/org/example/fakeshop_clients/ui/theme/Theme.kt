package org.example.fakeshop_clients.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.example.fakeshop_clients.core.design.DesignTokens.Colors

private val LightColorScheme = lightColorScheme(
    primary = Color(Colors.Light.PRIMARY),
    onPrimary = Color(Colors.Light.ON_PRIMARY),
    primaryContainer = Color(Colors.Light.PRIMARY_CONTAINER),
    onPrimaryContainer = Color(Colors.Light.ON_PRIMARY_CONTAINER),
    secondary = Color(Colors.Light.SECONDARY),
    onSecondary = Color(Colors.Light.ON_SECONDARY),
    secondaryContainer = Color(Colors.Light.SECONDARY_CONTAINER),
    onSecondaryContainer = Color(Colors.Light.ON_SECONDARY_CONTAINER),
    background = Color(Colors.Light.BACKGROUND),
    onBackground = Color(Colors.Light.ON_BACKGROUND),
    surface = Color(Colors.Light.SURFACE),
    surfaceVariant = Color(Colors.Light.SURFACE_VARIANT),
    onSurface = Color(Colors.Light.ON_SURFACE),
    onSurfaceVariant = Color(Colors.Light.ON_SURFACE_VARIANT),
    surfaceContainer = Color(Colors.Light.SURFACE_CONTAINER),
    surfaceContainerLow = Color(Colors.Light.SURFACE_CONTAINER_LOW),
    surfaceContainerLowest = Color(Colors.Light.SURFACE_CONTAINER_LOWEST),
    surfaceContainerHigh = Color(Colors.Light.SURFACE_CONTAINER_HIGH),
    surfaceContainerHighest = Color(Colors.Light.SURFACE_CONTAINER_HIGHEST),
    error = Color(Colors.Light.ERROR),
    onError = Color(Colors.Light.ON_ERROR),
    errorContainer = Color(Colors.Light.ERROR_CONTAINER),
    onErrorContainer = Color(Colors.Light.ON_ERROR_CONTAINER),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(Colors.Dark.PRIMARY),
    onPrimary = Color(Colors.Dark.ON_PRIMARY),
    primaryContainer = Color(Colors.Dark.PRIMARY_CONTAINER),
    onPrimaryContainer = Color(Colors.Dark.ON_PRIMARY_CONTAINER),
    secondary = Color(Colors.Dark.SECONDARY),
    onSecondary = Color(Colors.Dark.ON_SECONDARY),
    secondaryContainer = Color(Colors.Dark.SECONDARY_CONTAINER),
    onSecondaryContainer = Color(Colors.Dark.ON_SECONDARY_CONTAINER),
    background = Color(Colors.Dark.BACKGROUND),
    onBackground = Color(Colors.Dark.ON_BACKGROUND),
    surface = Color(Colors.Dark.SURFACE),
    surfaceVariant = Color(Colors.Dark.SURFACE_VARIANT),
    onSurface = Color(Colors.Dark.ON_SURFACE),
    onSurfaceVariant = Color(Colors.Dark.ON_SURFACE_VARIANT),
    surfaceContainer = Color(Colors.Dark.SURFACE_CONTAINER),
    surfaceContainerLow = Color(Colors.Dark.SURFACE_CONTAINER_LOW),
    surfaceContainerLowest = Color(Colors.Dark.SURFACE_CONTAINER_LOWEST),
    surfaceContainerHigh = Color(Colors.Dark.SURFACE_CONTAINER_HIGH),
    surfaceContainerHighest = Color(Colors.Dark.SURFACE_CONTAINER_HIGHEST),
    error = Color(Colors.Dark.ERROR),
    onError = Color(Colors.Dark.ON_ERROR),
    errorContainer = Color(Colors.Dark.ERROR_CONTAINER),
    onErrorContainer = Color(Colors.Dark.ON_ERROR_CONTAINER),
)

@Composable
fun FakeShopTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
