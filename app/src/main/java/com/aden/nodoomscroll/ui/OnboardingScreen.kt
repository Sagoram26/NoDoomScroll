package com.aden.nodoomscroll.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.aden.nodoomscroll.data.ModeLibreConfig
import com.aden.nodoomscroll.ui.theme.*

@Composable
fun OnboardingScreen(
    vm: AppViewModel,
    onComplete: () -> Unit
) {
    var step by remember { mutableIntStateOf(1) } // 1..7
    val c = LocalNdsColors.current

    // Lifted mode-libre choices (écran 4). Index → value maps below.
    var dureeIdx by remember { mutableIntStateOf(2) }   // default 15 min
    var freqIdx by remember { mutableIntStateOf(0) }    // default 1×/jour

    Column(Modifier.fillMaxSize().padding(horizontal = Space.s6, vertical = Space.s8)) {
        // Progress bar (7 segments)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            for (i in 1..7) {
                Box(Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp))
                    .background(if (i <= step) c.accent else c.accentSoft)
                )
            }
        }
        Text("$step/7", style = NdsType.labelMedium, color = c.textTertiary, modifier = Modifier.padding(top = Space.s2).align(Alignment.End))

        Spacer(Modifier.height(Space.s6))

        Box(Modifier.weight(1f)) {
            when (step) {
                1 -> Onboarding1_Bienvenue()
                2 -> Onboarding2_Permissions()
                3 -> Onboarding3_Apps()
                4 -> Onboarding4_ModeLibre(dureeIdx, { dureeIdx = it }, freqIdx, { freqIdx = it })
                5 -> Onboarding5_Temps()
                6 -> Onboarding6_Rapport()
                7 -> Onboarding7_Confirm()
            }
        }

        Spacer(Modifier.height(Space.s4))
        PrimaryButton(
            text = if (step == 7) "Activer NoDoomScroll" else "Continuer",
            onClick = {
                if (step < 7) step++ else {
                    val duration = listOf(5, 10, 15, 30)[dureeIdx]
                    val frequency = listOf(ModeLibreConfig.DAILY, ModeLibreConfig.WEEKLY, ModeLibreConfig.NEVER)[freqIdx]
                    vm.completeOnboarding(
                        ModeLibreConfig(
                            id = 0,
                            durationMin = duration,
                            frequency = frequency,
                            windowStartMin = 12 * 60,   // 12h–14h default from mockup
                            windowEndMin = 14 * 60,
                            activationDelayMin = 10,
                            isLocked = true,
                        )
                    )
                    onComplete()
                }
            }
        )
    }
}

@Composable
private fun Onboarding1_Bienvenue() {
    val c = LocalNdsColors.current
    Column {
        Text("Reprends le contrôle, calmement.", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("NoDoomScroll bloque le scroll infini — feed, Reels, Shorts — sans toucher à tes messages ni à tes publications.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.weight(1f))
        // visual placeholder
        Box(Modifier.fillMaxWidth().height(200.dp).glass(Radius.card), contentAlignment = Alignment.Center) {
            Text("✨", style = NdsType.displayLarge)
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun Onboarding2_Permissions() {
    val c = LocalNdsColors.current
    val ctx = LocalContext.current
    Column {
        Text("Trois autorisations nécessaires", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("NoDoomScroll a besoin de ça pour fonctionner. Tout reste sur ton téléphone, rien n'est envoyé ailleurs.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Column {
                Text("Détecter l'app affichée", style = NdsType.labelLarge, color = c.textPrimary)
                Text("Pour savoir quand bloquer", style = NdsType.bodySmall, color = c.textSecondary)
                Spacer(Modifier.height(Space.s3))
                SecondaryButton("Autoriser", onClick = { ctx.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)) })
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Column {
                Text("Afficher par-dessus les apps", style = NdsType.labelLarge, color = c.textPrimary)
                Text("Pour montrer l'écran de blocage", style = NdsType.bodySmall, color = c.textSecondary)
                Spacer(Modifier.height(Space.s3))
                SecondaryButton("Autoriser", onClick = { ctx.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)) })
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Column {
                Text("Protection contre la désinstallation", style = NdsType.labelLarge, color = c.textPrimary)
                Text("Pour que le blocage tienne", style = NdsType.bodySmall, color = c.textSecondary)
                Spacer(Modifier.height(Space.s3))
                SecondaryButton("Autoriser", onClick = { /* Device Admin intent */ })
            }
        }
    }
}

@Composable
private fun Onboarding3_Apps() {
    val c = LocalNdsColors.current
    Column {
        Text("Qu'est-ce qu'on bloque ?", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("Choisis les apps. On bloque juste le contenu addictif, tu gardes le reste.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Text("Instagram", style = NdsType.labelLarge, color = c.textPrimary)
            Text("Bloqué : Feed, Reels, Explore · Gardé : Messages, Stories, Profil", style = NdsType.bodySmall, color = c.textSecondary)
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Text("YouTube", style = NdsType.labelLarge, color = c.textPrimary)
            Text("Bloqué : Shorts, Page d'accueil · Gardé : Abonnements, Recherche, Vidéos", style = NdsType.bodySmall, color = c.textSecondary)
        }
        Spacer(Modifier.height(12.dp))
        Text("Une fois confirmé, on ne pourra plus retirer une app.", style = NdsType.bodySmall, color = c.warn, modifier = Modifier.padding(top = Space.s4))
    }
}

@Composable
private fun Onboarding4_ModeLibre(
    duree: Int, onDuree: (Int) -> Unit,
    freq: Int, onFreq: (Int) -> Unit,
) {
    val c = LocalNdsColors.current
    Column {
        Text("Ta soupape de sécurité", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("Le mode libre lève les blocages un court instant. Configure-le maintenant — après, c'est verrouillé.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Column {
                Text("Durée par session", style = NdsType.labelLarge, color = c.textPrimary)
                Spacer(Modifier.height(Space.s3))
                Row(Modifier.fillMaxWidth().glass(Radius.pill).padding(4.dp)) {
                    listOf("5 min", "10 min", "15 min", "30 min").forEachIndexed { i, title ->
                        Box(Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(Radius.pill)).background(if (duree == i) c.accent else Color.Transparent).clickable { onDuree(i) }, contentAlignment = Alignment.Center) {
                            Text(title, color = if (duree == i) c.onAccent else c.textSecondary, style = NdsType.labelMedium)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Column {
                Text("Fréquence", style = NdsType.labelLarge, color = c.textPrimary)
                Spacer(Modifier.height(Space.s3))
                Row(Modifier.fillMaxWidth().glass(Radius.pill).padding(4.dp)) {
                    listOf("1×/jour", "1×/semaine", "Jamais").forEachIndexed { i, title ->
                        Box(Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(Radius.pill)).background(if (freq == i) c.accent else Color.Transparent).clickable { onFreq(i) }, contentAlignment = Alignment.Center) {
                            Text(title, color = if (freq == i) c.onAccent else c.textSecondary, style = NdsType.labelMedium)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(12.dp))
        Text("Ces réglages ne pourront plus changer.", style = NdsType.bodySmall, color = c.warn)
    }
}

@Composable
private fun Onboarding5_Temps() {
    val c = LocalNdsColors.current
    Column {
        Text("Combien de temps tu y passes ?", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("Une estimation, juste pour calculer ce que tu récupères.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Text("Instagram", style = NdsType.labelLarge, color = c.textPrimary)
            NdsProgressBar(0.3f, modifier = Modifier.padding(top = Space.s3))
            Text("45 min / jour", style = NdsType.bodySmall, color = c.textSecondary, modifier = Modifier.padding(top = 4.dp).align(Alignment.End))
        }
    }
}

@Composable
private fun Onboarding6_Rapport() {
    val c = LocalNdsColors.current
    Column {
        Text("Ton bilan du dimanche", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s4))
        Text("Chaque semaine, un résumé calme de tes progrès.", style = NdsType.bodyLarge, color = c.textSecondary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Text("Jour", style = NdsType.labelLarge, color = c.textPrimary)
            Row(Modifier.fillMaxWidth().padding(top = Space.s2), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Format.dayAbbrev.forEach { d ->
                    NdsChip(d, selected = d == "Dim", onClick = {})
                }
            }
        }
    }
}

@Composable
private fun Onboarding7_Confirm() {
    val c = LocalNdsColors.current
    Column {
        Text("Tout est prêt", style = NdsType.headlineLarge, color = c.textPrimary)
        Spacer(Modifier.height(Space.s6))

        GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
            Text("Apps filtrées", style = NdsType.labelLarge, color = c.textPrimary)
            Text("Instagram, YouTube", style = NdsType.bodySmall, color = c.textSecondary)
        }
        Spacer(Modifier.height(12.dp))
        Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(22.dp)).border(2.dp, c.accent, RoundedCornerShape(22.dp)).padding(16.dp)) {
            Column {
                Text("Important", style = NdsType.labelLarge, color = c.accent)
                Spacer(Modifier.height(4.dp))
                Text("Ces réglages sont verrouillés une fois activé. Tu pourras toujours désinstaller l'app, mais pas contourner le blocage tant qu'elle est là.", style = NdsType.bodySmall, color = c.textPrimary)
            }
        }
    }
}
