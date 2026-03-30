package com.nmd.eventCalendar.theme

import android.os.Build
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RippleConfiguration
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val lightColorScheme = lightColorScheme()

private val darkColorScheme = darkColorScheme()

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Resolve the active color scheme:
    // - Use dynamic colors on Android 12+ if enabled
    // - Otherwise fall back to the static light/dark color schemes
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> darkColorScheme
        else -> lightColorScheme
    }

    // Resolve ripple color and alpha values for the current theme
    val rippleColorAndAlpha = rippleColorForThemeDefault(darkTheme)

    // Global ripple configuration for Material components (e.g., Buttons, Cards)
    // This overrides M3 ripple defaults by applying our own color and alpha settings.
    val appRippleConfiguration = remember(darkTheme) {
        RippleConfiguration(
            color = rippleColorAndAlpha.first,
            rippleAlpha = RippleAlpha(
                pressedAlpha = rippleColorAndAlpha.second,
                focusedAlpha = rippleColorAndAlpha.second,
                draggedAlpha = rippleColorAndAlpha.second,
                hoveredAlpha = rippleColorAndAlpha.second
            )
        )
    }

    // Global ripple indication for Foundation clickables (Modifier.clickable)
    // Ensures consistent ripple appearance across Material and non-Material components.
    val appIndication = remember(darkTheme) {
        ripple(color = rippleColorAndAlpha.first.copy(alpha = rippleColorAndAlpha.second))
    }

    // Provide ripple configuration and indication to all children in the theme
    CompositionLocalProvider(
        LocalRippleConfiguration provides appRippleConfiguration,
        LocalIndication provides appIndication
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}

@Composable
fun rippleColorForThemeDefault(darkTheme: Boolean): Pair<Color, Float> {
    // Base ripple color for light and dark themes.
    // Alpha is applied separately, so we return only the base tint here.
    return if (darkTheme) {
        // Light ripple tint for dark surfaces and 25% opacity
        Pair(Color.White, 0.25f)
    } else {
        // Dark ripple tint for light surfaces and 12% opacity
        Pair(Color.Black, 0.12f)
    }
}