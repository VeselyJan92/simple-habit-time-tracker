package com.imfibit.activitytracker.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

data class AppColors(
    // Material Colors
    val primary: Color,
    val onPrimary: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val surfaceContainerLow: Color,
    val error: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,

    // Custom / Non-Material Colors
    val onSurfaceTitle: Color,
    val lightBackground: Color,
    val divider: Color,
    val iconBackground: Color,
    val warning: Color,
    val success: Color,
    val draggingBackground: Color,
    val bulletColor: Color,
    
    val statusCompleted: Color,
    val statusNotCompleted: Color,
    val statusNeutral: Color,

    val superLight: Color,
    val chipGraySelected: Color,
    val chipGrayUnselected: Color,
    val appAccent: Color
)

val lightAppColors = AppColors(
    // Material Colors
    primary = Color(0xFF6B5B95),
    onPrimary = Color.White,
    background = Color(0xFFe4eaee),
    surface = Color(0xFFF1F1F1),
    onSurface = Color(0xFF1A202C),
    surfaceVariant = Color(0xFFE2E8F0),
    onSurfaceVariant = Color(0xFF4A5568),
    outline = Color(0xFFA0AEC0),
    outlineVariant = Color(0xFFCBD5E0),
    surfaceContainerLow = Color.White,
    error = Color(0xFFE53E3E),
    errorContainer = Color(0xFFFC8181),
    onErrorContainer = Color(0xFFFFF5F5),

    // Custom / Non-Material Colors
    onSurfaceTitle = Color(0xFF2D3748),
    lightBackground = Color(0xFFF4F5F8),
    divider = Color(0xFFEDF2F7),
    iconBackground = Color(0xFFE8EAF1),
    warning = Color(0xFFFFB300),
    success = Color(0xFF48BB78),
    draggingBackground = Color(0xFFD9E0E5),
    bulletColor = Color(0xFF1A365D),
    statusCompleted = Color(0xFF59BF2D),
    statusNotCompleted = Color(0xFFFF9800),
    statusNeutral = Color(0xFFE0E0E0),
    superLight = Color(0xFFF3F3F3),
    chipGraySelected = Color(0xFFBDBDBD),
    chipGrayUnselected = Color(0xFFE0E0E0),
    appAccent = Color(0xFF4DB6AC)
)

val darkAppColors = AppColors(
    // Material Colors
    primary = Color(0xFFB19CD9),
    onPrimary = Color(0xFF1A202C),
    background = Color(0xFF12141A),
    surface = Color(0xFF1E212B),
    onSurface = Color(0xFFE2E8F0),
    surfaceVariant = Color(0xFF2D3748),
    onSurfaceVariant = Color(0xFFA0AEC0),
    outline = Color(0xFF718096),
    outlineVariant = Color(0xFF4A5568),
    surfaceContainerLow = Color(0xFF242733),
    error = Color(0xFFFC8181),
    errorContainer = Color(0xFF742A2A),
    onErrorContainer = Color(0xFF3C1D1D),

    // Custom / Non-Material Colors
    onSurfaceTitle = Color(0xFFF7FAFC),
    lightBackground = Color(0xFF1A1D24),
    divider = Color(0xFF2D3748),
    iconBackground = Color(0xFF2D3748),
    warning = Color(0xFFF6E05E),
    success = Color(0xFF48BB78),
    draggingBackground = Color(0xFF333A4A),
    bulletColor = Color(0xFF90CDF4),
    statusCompleted = Color(0xFF4CAF50),
    statusNotCompleted = Color(0xFFED8936),
    statusNeutral = Color(0xFF4A5568),
    superLight = Color(0xFF2D3748),
    chipGraySelected = Color(0xFF718096),
    chipGrayUnselected = Color(0xFF4A5568),
    appAccent = Color(0xFF319795)
)

val LocalAppColors = staticCompositionLocalOf<AppColors> {
    error("No AppColors provided")
}

object AppTheme {
    val colors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    @Composable
    operator fun invoke(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val appColors = if (darkTheme) darkAppColors else lightAppColors

        val colorScheme = if (darkTheme) {
            darkColorScheme(
                primary = appColors.primary,
                onPrimary = appColors.onPrimary,
                background = appColors.background,
                surface = appColors.surface,
                onSurface = appColors.onSurface,
                surfaceVariant = appColors.surfaceVariant,
                onSurfaceVariant = appColors.onSurfaceVariant,
                outline = appColors.outline,
                outlineVariant = appColors.outlineVariant,
                surfaceContainerLow = appColors.surfaceContainerLow,
                error = appColors.error,
                errorContainer = appColors.errorContainer,
                onErrorContainer = appColors.onErrorContainer
            )
        } else {
            lightColorScheme(
                primary = appColors.primary,
                onPrimary = appColors.onPrimary,
                background = appColors.background,
                surface = appColors.surface,
                onSurface = appColors.onSurface,
                surfaceVariant = appColors.surfaceVariant,
                onSurfaceVariant = appColors.onSurfaceVariant,
                outline = appColors.outline,
                outlineVariant = appColors.outlineVariant,
                surfaceContainerLow = appColors.surfaceContainerLow,
                error = appColors.error,
                errorContainer = appColors.errorContainer,
                onErrorContainer = appColors.onErrorContainer
            )
        }

        CompositionLocalProvider(
            LocalAppColors provides appColors
        ) {
            MaterialTheme(
                colorScheme = colorScheme
            ) {
                content()
            }
        }
    }
}
