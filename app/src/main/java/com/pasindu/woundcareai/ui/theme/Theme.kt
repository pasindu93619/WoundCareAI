package com.pasindu.woundcareai.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// STRICT LIGHT THEME
// Using the Vibrant Purple & Teal palette
private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    onPrimary = OnPrimaryWhite,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = SecondaryTeal,
    onSecondary = OnSecondaryBlack, // Black text on Teal is high contrast
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    error = ErrorRed,
    onError = OnErrorWhite,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = BackgroundNeutral,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    outline = Outline
)

@Composable
fun WoundCareAITheme(
    // Ignore system dark mode setting
    darkTheme: Boolean = false,
    // Disable dynamic color to enforce our Purple brand
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Status bar matches the Primary Purple
            window.statusBarColor = colorScheme.primary.toArgb()

            // Icons are light (white) because the purple is dark/vibrant
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}