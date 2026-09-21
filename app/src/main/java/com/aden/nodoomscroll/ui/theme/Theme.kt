package com.aden.nodoomscroll.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

val LocalNdsColors = staticCompositionLocalOf { DarkColors }

/** Spacing scale from style-system.md (base 4dp). */
object Space {
    val s1 = 4.dp
    val s2 = 12.dp
    val s3 = 16.dp
    val s4 = 20.dp
    val s6 = 24.dp
    val s8 = 32.dp
}

/** Corner radii from style-system.md. */
object Radius {
    val card = 28.dp
    val cardLg = 32.dp
    val button = 22.dp
    val pill = 999.dp
    val chip = 16.dp
    val small = 12.dp
    val streak = 999.dp  // full circle
}

@Composable
fun NoDoomScrollTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val nds = if (darkTheme) DarkColors else LightColors
    val scheme = if (darkTheme) {
        darkColorScheme(
            primary = nds.accent,
            onPrimary = nds.onAccent,
            background = nds.bgBase,
            surface = nds.bgBase,
            onBackground = nds.textPrimary,
            onSurface = nds.textPrimary,
            error = nds.warn,
        )
    } else {
        lightColorScheme(
            primary = nds.accent,
            onPrimary = nds.onAccent,
            background = nds.bgBase,
            surface = nds.bgBase,
            onBackground = nds.textPrimary,
            onSurface = nds.textPrimary,
            error = nds.warn,
        )
    }
    CompositionLocalProvider(LocalNdsColors provides nds) {
        MaterialTheme(colorScheme = scheme, typography = NdsType, content = content)
    }
}
