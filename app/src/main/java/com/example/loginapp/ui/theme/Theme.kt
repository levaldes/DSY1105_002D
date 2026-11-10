package com.example.loginapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ColorTokens.DarkPrimary,
    onPrimary = ColorTokens.DarkOnPrimary,
    primaryContainer = ColorTokens.DarkPrimaryContainer,
    onPrimaryContainer = ColorTokens.DarkOnPrimaryContainer,
    secondary = GuardianCyan,
    onSecondary = GuardianNavy,
    secondaryContainer = GuardianDarkSurfaceVariant,
    onSecondaryContainer = GuardianDarkText,
    tertiary = GuardianAmber,
    onTertiary = GuardianNavy,
    background = GuardianDarkBackground,
    onBackground = GuardianDarkText,
    surface = GuardianDarkSurface,
    onSurface = GuardianDarkText,
    surfaceVariant = GuardianDarkSurfaceVariant,
    onSurfaceVariant = GuardianDarkMuted,
    outline = GuardianDarkOutline,
    outlineVariant = GuardianDarkOutline,
    error = ColorTokens.DarkError,
    onError = GuardianNavy,
    errorContainer = ColorTokens.DarkErrorContainer,
    onErrorContainer = ColorTokens.DarkOnErrorContainer
)

private val LightColorScheme = lightColorScheme(
    primary = GuardianIndigo,
    onPrimary = GuardianSurface,
    primaryContainer = ColorTokens.LightPrimaryContainer,
    onPrimaryContainer = ColorTokens.LightOnPrimaryContainer,
    secondary = GuardianTeal,
    onSecondary = GuardianSurface,
    secondaryContainer = ColorTokens.LightSecondaryContainer,
    onSecondaryContainer = ColorTokens.LightOnSecondaryContainer,
    tertiary = GuardianAmber,
    onTertiary = GuardianSurface,
    background = GuardianBackground,
    onBackground = GuardianText,
    surface = GuardianSurface,
    onSurface = GuardianText,
    surfaceVariant = GuardianSurfaceVariant,
    onSurfaceVariant = GuardianMuted,
    outline = GuardianOutline,
    outlineVariant = GuardianOutline,
    error = GuardianRed,
    onError = GuardianSurface,
    errorContainer = ColorTokens.LightErrorContainer,
    onErrorContainer = ColorTokens.LightOnErrorContainer
)

@Composable
fun LoginAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Desactivado por defecto para conservar la identidad visual de Modo Guardián.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

private object ColorTokens {
    val LightPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE0E7FF)
    val LightOnPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF1E1B4B)
    val LightSecondaryContainer = androidx.compose.ui.graphics.Color(0xFFCCFBF1)
    val LightOnSecondaryContainer = androidx.compose.ui.graphics.Color(0xFF134E4A)
    val LightErrorContainer = androidx.compose.ui.graphics.Color(0xFFFEE2E2)
    val LightOnErrorContainer = androidx.compose.ui.graphics.Color(0xFF7F1D1D)

    val DarkPrimary = androidx.compose.ui.graphics.Color(0xFFA5B4FC)
    val DarkOnPrimary = androidx.compose.ui.graphics.Color(0xFF1E1B4B)
    val DarkPrimaryContainer = androidx.compose.ui.graphics.Color(0xFF312E81)
    val DarkOnPrimaryContainer = androidx.compose.ui.graphics.Color(0xFFE0E7FF)
    val DarkError = androidx.compose.ui.graphics.Color(0xFFF87171)
    val DarkErrorContainer = androidx.compose.ui.graphics.Color(0xFF7F1D1D)
    val DarkOnErrorContainer = androidx.compose.ui.graphics.Color(0xFFFEE2E2)
}
