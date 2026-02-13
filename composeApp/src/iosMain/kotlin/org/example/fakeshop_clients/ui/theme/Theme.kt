package org.example.fakeshop_clients.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.example.fakeshop_clients.core.design.DesignTokens.Colors

private val LightColorScheme = lightColorScheme(
    primary = Color(Colors.Light.Primary),
    onPrimary = Color(Colors.Light.OnPrimary),
    primaryContainer = Color(Colors.Light.PrimaryContainer),
    onPrimaryContainer = Color(Colors.Light.OnPrimaryContainer),
    secondary = Color(Colors.Light.Secondary),
    onSecondary = Color(Colors.Light.OnSecondary),
    secondaryContainer = Color(Colors.Light.SecondaryContainer),
    onSecondaryContainer = Color(Colors.Light.OnSecondaryContainer),
    background = Color(Colors.Light.Background),
    onBackground = Color(Colors.Light.OnBackground),
    surface = Color(Colors.Light.Surface),
    surfaceVariant = Color(Colors.Light.SurfaceVariant),
    onSurface = Color(Colors.Light.OnSurface),
    onSurfaceVariant = Color(Colors.Light.OnSurfaceVariant),
    surfaceContainer = Color(Colors.Light.Surface),
    surfaceContainerLow = Color(Colors.Light.Surface),
    surfaceContainerLowest = Color(Colors.Light.Surface),
    surfaceContainerHigh = Color(Colors.Light.Surface),
    surfaceContainerHighest = Color(Colors.Light.Surface),
    error = Color(Colors.Light.Error),
    onError = Color(Colors.Light.OnError),
    errorContainer = Color(Colors.Light.ErrorContainer),
    onErrorContainer = Color(Colors.Light.OnErrorContainer),
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(Colors.Dark.Primary),
    onPrimary = Color(Colors.Dark.OnPrimary),
    primaryContainer = Color(Colors.Dark.PrimaryContainer),
    onPrimaryContainer = Color(Colors.Dark.OnPrimaryContainer),
    secondary = Color(Colors.Dark.Secondary),
    onSecondary = Color(Colors.Dark.OnSecondary),
    secondaryContainer = Color(Colors.Dark.SecondaryContainer),
    onSecondaryContainer = Color(Colors.Dark.OnSecondaryContainer),
    background = Color(Colors.Dark.Background),
    onBackground = Color(Colors.Dark.OnBackground),
    surface = Color(Colors.Dark.Surface),
    surfaceVariant = Color(Colors.Dark.SurfaceVariant),
    onSurface = Color(Colors.Dark.OnSurface),
    onSurfaceVariant = Color(Colors.Dark.OnSurfaceVariant),
    surfaceContainer = Color(Colors.Dark.Surface),
    surfaceContainerLow = Color(Colors.Dark.Surface),
    surfaceContainerLowest = Color(Colors.Dark.Surface),
    surfaceContainerHigh = Color(Colors.Dark.Surface),
    surfaceContainerHighest = Color(Colors.Dark.Surface),
    error = Color(Colors.Dark.Error),
    onError = Color(Colors.Dark.OnError),
    errorContainer = Color(Colors.Dark.ErrorContainer),
    onErrorContainer = Color(Colors.Dark.OnErrorContainer),
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
