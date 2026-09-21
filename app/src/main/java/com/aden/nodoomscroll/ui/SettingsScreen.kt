package com.aden.nodoomscroll.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.aden.nodoomscroll.data.ModeLibreConfig
import com.aden.nodoomscroll.ui.theme.*

@Composable
fun SettingsScreen(vm: AppViewModel) {
    val c = LocalNdsColors.current
    val settings by vm.settings.collectAsState()
    val config by vm.modeLibreConfig.collectAsState()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal = Space.s4, vertical = Space.s6),
        verticalArrangement = Arrangement.spacedBy(Space.s6)
    ) {
        Text("Réglages", style = NdsType.headlineLarge, color = c.textPrimary)

        // Section 1 — Affichage
        SectionLabel("AFFICHAGE")
        GlassCard {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("Mode niveaux de gris", style = NdsType.labelLarge, color = c.textPrimary)
                    Text("Passe l'écran en gris quand tu ouvres une app bloquée.", style = NdsType.bodySmall, color = c.textSecondary)
                }
                Spacer(Modifier.width(Space.s3))
                // ponytail: real grayscale needs WRITE_SECURE_SETTINGS (ADB one-time). Toggle stores
                // intent; applying it is a Phase-3 concern. Persist the flag now.
                Switch(
                    checked = settings?.grayscaleEnabled ?: false,
                    onCheckedChange = { vm.setGrayscale(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = c.onAccent, checkedTrackColor = c.accent)
                )
            }
        }

        // Section 2 — Configuration (lecture seule)
        SectionLabel("CONFIGURATION")
        GlassCard {
            LockedRow("Mode libre", config?.let { fmtModeLibre(it) } ?: "Non configuré")
        }
        GlassCard {
            LockedRow("Rapport hebdomadaire", settings?.let { Format.weeklySlot(it.weeklyReportDay, it.weeklyReportHour) } ?: "Dimanche 20h")
        }

        // Section 3 — À propos
        SectionLabel("À PROPOS")
        GlassCard {
            Column(verticalArrangement = Arrangement.spacedBy(Space.s3)) {
                StatRow("Version NoDoomScroll", "1.0.0")
                StatRow("Données", "100% local")
                Text("Aucune donnée n'est envoyée ailleurs.", style = NdsType.bodySmall, color = c.textTertiary)
                Text("Politique de confidentialité", style = NdsType.bodySmall, color = c.accent)
            }
        }

        // Section 4 — Désinstallation
        SectionLabel("DÉSINSTALLATION")
        GlassCard {
            Column {
                Text("Comment désinstaller NoDoomScroll ?", style = NdsType.labelLarge, color = c.textPrimary)
                Spacer(Modifier.height(Space.s2))
                listOf(
                    "1. Aller à Paramètres > Apps > NoDoomScroll",
                    "2. Appuyer sur « Avancé » > « Administrateur d'appareil »",
                    "3. Révoquer l'accès administrateur",
                    "4. Désinstaller l'app",
                ).forEach {
                    Text(it, style = NdsType.bodySmall, color = c.textSecondary, modifier = Modifier.padding(vertical = 2.dp))
                }
                Spacer(Modifier.height(Space.s2))
                Text("Une fois désinstallé, tous les blocages seront levés.", style = NdsType.bodySmall, color = c.textTertiary)
            }
        }
        Spacer(Modifier.height(Space.s8))
    }
}

@Composable
private fun LockedRow(label: String, value: String) {
    val c = LocalNdsColors.current
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(label, style = NdsType.labelLarge, color = c.textPrimary)
            Text(value, style = NdsType.bodySmall, color = c.textSecondary)
        }
        Box(
            Modifier.clip(RoundedCornerShape(Radius.pill)).background(c.accentSoft)
                .padding(horizontal = Space.s2, vertical = 4.dp)
        ) {
            Text("Verrouillé", style = NdsType.labelSmall, color = c.textPrimary)
        }
    }
}

private fun fmtModeLibre(cfg: ModeLibreConfig): String {
    val freq = when (cfg.frequency) {
        ModeLibreConfig.DAILY -> "1×/jour"
        ModeLibreConfig.WEEKLY -> "1×/semaine"
        else -> "Jamais"
    }
    val window = if (cfg.windowStartMin >= 0 && cfg.windowEndMin >= 0)
        "${cfg.windowStartMin / 60}h–${cfg.windowEndMin / 60}h" else "Tout le temps"
    return "${cfg.durationMin} min · $freq · $window · ${cfg.activationDelayMin} min délai"
}
