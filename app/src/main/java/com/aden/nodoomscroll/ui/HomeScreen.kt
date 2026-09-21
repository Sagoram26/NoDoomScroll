package com.aden.nodoomscroll.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.GridView
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material3.Icon
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import com.aden.nodoomscroll.data.ModeLibreController
import com.aden.nodoomscroll.data.NdsRepository.ModeLibreStatus
import com.aden.nodoomscroll.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    vm: AppViewModel,
    onNavAnalytics: () -> Unit,
    onNavApps: () -> Unit,
    onNavSettings: () -> Unit,
) {
    val streak by vm.streak.collectAsState()
    val savedSecs by vm.secondsSavedToday.collectAsState()
    val blockedCount by vm.blockedToday.collectAsState()
    val mlStatus by vm.modeLibreStatus.collectAsState()
    val mlRuntime by vm.modeLibreRuntime.collectAsState()
    val trend by vm.trend24h.collectAsState()
    val c = LocalNdsColors.current

    // 1 Hz tick so the mode-libre countdown label recomputes while a session/delay runs.
    var tick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(mlRuntime.phase) {
        while (mlRuntime.phase != ModeLibreController.Phase.Idle) { tick++; delay(1000) }
    }

    Column(Modifier.fillMaxSize().padding(horizontal = Space.s4)) {
        // Top bar — greeting + 3 discreet nav icon buttons (mockup layout)
        Row(
            Modifier.fillMaxWidth().padding(top = Space.s6),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Bonjour", style = NdsType.titleLarge, color = c.textPrimary)
                Text(Format.today(), style = NdsType.bodySmall, color = c.textSecondary)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(Space.s2)) {
                NavIcon(Icons.Rounded.BarChart, "Stats", onNavAnalytics)
                NavIcon(Icons.Rounded.GridView, "Apps", onNavApps)
                NavIcon(Icons.Rounded.Tune, "Réglages", onNavSettings)
            }
        }

        Spacer(Modifier.weight(1f))

        // Streak Signature
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            val breath by rememberInfiniteTransition().animateFloat(
                initialValue = 1f, targetValue = 1.035f,
                animationSpec = infiniteRepeatable(tween(4000, easing = CubicBezierEasing(0.4f, 0f, 0.6f, 1f)), RepeatMode.Reverse)
            )
            // Halo
            Box(Modifier.size(384.dp).graphicsLayer { scaleX = breath; scaleY = breath; alpha = if (breath > 1.01f) 1f else 0.5f }
                .blur(if (c.isDark) 50.dp else 28.dp)
                .background(Brush.radialGradient(listOf(c.streakGlow, Color.Transparent), radius = 384f/2))
            )
            // Glass streak circle
            Box(Modifier.size(228.dp).graphicsLayer { scaleX = breath; scaleY = breath }.glass(Radius.streak), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("${streak?.currentStreakDays ?: 0}", style = NdsStreakNumber, color = c.accent)
                    Text("jours", style = NdsType.labelLarge, color = c.textSecondary)
                }
            }
        }

        Spacer(Modifier.weight(1f))

        // Today Stats Card
        GlassCard(padding = PaddingValues(22.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    SectionLabel("TEMPS ÉCONOMISÉ AUJOURD'HUI", Modifier.padding(bottom = Space.s2))
                    Text(Format.duration(savedSecs), style = NdsType.displayLarge, color = c.accent)
                }
                Column(Modifier.weight(1f)) {
                    SectionLabel("TENTATIVES BLOQUÉES", Modifier.padding(bottom = Space.s2))
                    Text("$blockedCount", style = NdsType.displayLarge, color = c.accent)
                }
            }
        }

        Spacer(Modifier.height(Space.s3)) // 12dp card gap

        // Trend Card — 24h sparkline of blocked attempts
        GlassCard(padding = PaddingValues(22.dp)) {
            SectionLabel("TENDANCE · 24H", Modifier.padding(bottom = Space.s2))
            Sparkline(trend, Modifier.fillMaxWidth().height(40.dp))
        }

        Spacer(Modifier.height(Space.s3))

        // Mode Libre Button — reflects live phase (delay / active countdown) then availability.
        if (mlStatus !is ModeLibreStatus.NeverConfigured) {
            tick // read to recompose each tick
            val now = System.currentTimeMillis()
            val label = when (mlRuntime.phase) {
                ModeLibreController.Phase.Delay -> "Mode libre · ${mmss(mlRuntime.phaseEndsAt - now)}"
                ModeLibreController.Phase.Active -> "Actif · ${mmss(mlRuntime.phaseEndsAt - now)}"
                else -> when (mlStatus) {
                    is ModeLibreStatus.Available -> "Activer le mode libre"
                    is ModeLibreStatus.QuotaExhausted -> "Quota épuisé"
                    is ModeLibreStatus.OutOfWindow -> "Disponible à ${(mlStatus as ModeLibreStatus.OutOfWindow).startMin / 60}h"
                    else -> ""
                }
            }
            PrimaryButton(
                text = label,
                enabled = mlRuntime.phase == ModeLibreController.Phase.Idle && mlStatus is ModeLibreStatus.Available,
                onClick = { vm.startModeLibre() }
            )
        }

        Spacer(Modifier.height(Space.s6))
    }
}

@Composable
private fun NavIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, desc: String, onClick: () -> Unit) {
    val c = LocalNdsColors.current
    Box(
        Modifier.size(40.dp).clip(androidx.compose.foundation.shape.RoundedCornerShape(Radius.pill))
            .glass(Radius.pill).clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = desc, tint = c.textSecondary, modifier = Modifier.size(20.dp))
    }
}

private fun mmss(remainingMs: Long): String {
    val s = (remainingMs / 1000).coerceAtLeast(0)
    return "%d:%02d".format(s / 60, s % 60)
}

@Composable
private fun Sparkline(values: List<Int>, modifier: Modifier = Modifier) {
    val c = LocalNdsColors.current
    val max = (values.maxOrNull() ?: 0).coerceAtLeast(1)
    Canvas(modifier) {
        if (values.size < 2) return@Canvas
        val stepX = size.width / (values.size - 1)
        val pts = values.mapIndexed { i, v ->
            Offset(i * stepX, size.height - (v.toFloat() / max) * size.height)
        }
        for (i in 0 until pts.size - 1) {
            drawLine(c.accent, pts[i], pts[i + 1], strokeWidth = 3f, cap = StrokeCap.Round)
        }
    }
}
