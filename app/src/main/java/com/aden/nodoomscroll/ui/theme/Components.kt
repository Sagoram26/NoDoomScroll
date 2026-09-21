package com.aden.nodoomscroll.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * App background: vertical gradient base + a couple of soft radial "blobs" that drift. Approximates
 * the mesh/animated-blob background from style-system.md within Compose's gradient primitives.
 */
@Composable
fun NdsBackground(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val c = LocalNdsColors.current
    val transition = rememberInfiniteTransition(label = "bg")
    val drift by transition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(14000, easing = LinearEasing), RepeatMode.Reverse),
        label = "drift",
    )
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(c.gradTop, c.gradMid, c.gradBottom)))
            .drawBehind {
                val blob1 = ShaderBrush(
                    RadialGradientShader(
                        center = Offset(size.width * (0.20f + 0.12f * drift), size.height * 0.14f),
                        radius = size.width * 0.75f,
                        colors = listOf(c.accent.copy(alpha = if (c.isDark) 0.16f else 0.30f), Color.Transparent),
                        tileMode = TileMode.Clamp,
                    )
                )
                val blob2 = ShaderBrush(
                    RadialGradientShader(
                        center = Offset(size.width * (0.85f - 0.10f * drift), size.height * 0.82f),
                        radius = size.width * 0.85f,
                        colors = listOf(c.accent2.copy(alpha = if (c.isDark) 0.12f else 0.28f), Color.Transparent),
                        tileMode = TileMode.Clamp,
                    )
                )
                drawRect(blob1)
                drawRect(blob2)
            },
    ) { content() }
}

/** Glass surface fill + double border (bright top rim fading to soft bottom). */
@Composable
fun Modifier.glass(radius: Dp = Radius.card): Modifier {
    val c = LocalNdsColors.current
    val shape = RoundedCornerShape(radius)
    return this
        .clip(shape)
        .background(c.glassFill, shape)
        .border(BorderStroke(1.dp, Brush.verticalGradient(listOf(c.glassBorderTop, c.glassBorderBottom))), shape)
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    radius: Dp = Radius.card,
    padding: PaddingValues = PaddingValues(Space.s6),
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val base = modifier.fillMaxWidth().glass(radius)
    val clickable = if (onClick != null) base.clickable(onClick = onClick) else base
    Box(clickable.padding(padding)) { content() }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val c = LocalNdsColors.current
    val shape = RoundedCornerShape(Radius.button)
    val bg = if (enabled)
        Brush.horizontalGradient(listOf(c.accent, c.accent2))
    else Brush.horizontalGradient(listOf(c.textTertiary.copy(alpha = 0.4f), c.textTertiary.copy(alpha = 0.4f)))
    Box(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(shape)
            .background(bg, shape)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.onAccent, style = NdsType.labelLarge)
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalNdsColors.current
    Box(
        modifier
            .fillMaxWidth()
            .height(50.dp)
            .glass(Radius.button)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.textPrimary, style = NdsType.labelLarge)
    }
}

@Composable
fun GhostButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val c = LocalNdsColors.current
    Box(
        modifier
            .wrapContentSize()
            .clip(RoundedCornerShape(Radius.button))
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s3, vertical = Space.s2),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = c.textSecondary, style = NdsType.bodySmall)
    }
}

/** Filled segmented progress meter (analytics "réduction" bar, onboarding progress). */
@Composable
fun NdsProgressBar(fraction: Float, modifier: Modifier = Modifier, height: Dp = 8.dp) {
    val c = LocalNdsColors.current
    Box(
        modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(Radius.pill))
            .background(c.emphBg),
    ) {
        Box(
            Modifier
                .fillMaxWidth(fraction.coerceIn(0f, 1f))
                .height(height)
                .clip(RoundedCornerShape(Radius.pill))
                .background(Brush.horizontalGradient(listOf(c.accent, c.accent2))),
        )
    }
}

/** Rounded chip/pill with optional selected state (filter tabs, day picker). */
@Composable
fun NdsChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = LocalNdsColors.current
    val shape = RoundedCornerShape(Radius.chip)
    val bg = if (selected) Modifier.background(Brush.horizontalGradient(listOf(c.accent, c.accent2)), shape)
    else Modifier.glass(Radius.chip)
    Box(
        modifier
            .clip(shape)
            .then(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = Space.s3, vertical = 10.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            color = if (selected) c.onAccent else c.textSecondary,
            style = NdsType.labelMedium,
        )
    }
}

@Composable
fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color? = null,
) {
    val c = LocalNdsColors.current
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, color = c.textSecondary, style = NdsType.bodyMedium)
        Text(value, color = valueColor ?: c.textPrimary, style = NdsType.bodySmall.copy(fontWeight = FontWeight.Bold))
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    val c = LocalNdsColors.current
    Text(text.uppercase(), modifier = modifier, color = c.textTertiary, style = NdsType.labelSmall)
}

@Composable
fun ProvideContentColor(color: Color, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContentColor provides color, content = content)
}
