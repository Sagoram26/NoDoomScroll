package com.aden.nodoomscroll.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.aden.nodoomscroll.ui.theme.*

@Composable
fun AnalyticsScreen(vm: AppViewModel) {
    val c = LocalNdsColors.current
    var tab by remember { mutableIntStateOf(0) } // 0=Jour, 1=Semaine, 2=Mois

    val savedToday by vm.secondsSavedToday.collectAsState()
    val blockedToday by vm.blockedToday.collectAsState()
    val perApp by vm.perAppToday.collectAsState()
    val bypasses by vm.bypassHistory.collectAsState()

    val allTimeSecs by vm.allTimeSecondsSaved.collectAsState()
    val weekSecs by vm.weekSecondsSaved.collectAsState()
    val weekBlocked by vm.weekBlocked.collectAsState()
    val monthSecs by vm.monthSecondsSaved.collectAsState()
    val sessions by vm.allTimeSessions.collectAsState()
    val rate by vm.blockRate.collectAsState()
    val streak by vm.streak.collectAsState()

    LazyColumn(
        Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = Space.s4, end = Space.s4, top = Space.s6, bottom = Space.s8),
        verticalArrangement = Arrangement.spacedBy(Space.s6) // space-6 minor gap
    ) {
        item {
            Text("Tes stats", style = NdsType.headlineLarge, color = c.textPrimary)
            Spacer(Modifier.height(Space.s4))

            // Segmented tabs — Jour · Semaine · Mois · Total (mockup order)
            Row(Modifier.fillMaxWidth().glass(Radius.pill).padding(4.dp)) {
                listOf("Jour", "Semaine", "Mois", "Total").forEachIndexed { i, title ->
                    Box(
                        Modifier.weight(1f).height(32.dp).clip(RoundedCornerShape(Radius.pill))
                            .background(if (tab == i) c.accent else Color.Transparent)
                            .clickable { tab = i },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(title, color = if (tab == i) c.onAccent else c.textSecondary, style = NdsType.labelMedium)
                    }
                }
            }
            Spacer(Modifier.height(Space.s2)) // to make up space-8 total
        }

        if (tab == 0) {
            item {
                GlassCard {
                    SectionLabel("TEMPS ÉCONOMISÉ AUJOURD'HUI")
                    Text(Format.duration(savedToday), style = NdsType.displayLarge, color = c.accent)
                    Text("+0% vs hier", style = NdsType.bodySmall, color = c.textSecondary)
                }
            }
            item {
                GlassCard {
                    SectionLabel("TENTATIVES BLOQUÉES")
                    Text("$blockedToday", style = NdsType.displayLarge, color = c.accent)
                    Spacer(Modifier.height(Space.s3))
                    perApp.forEach { (app, count) ->
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(app, style = NdsType.bodySmall, color = c.textPrimary)
                            Text("$count", style = NdsType.bodySmall, color = c.textSecondary)
                        }
                    }
                }
            }
        }

        if (tab == 1) {
            item {
                GlassCard {
                    SectionLabel("TEMPS ÉCONOMISÉ CETTE SEM.")
                    Text(Format.duration(weekSecs), style = NdsType.displayLarge, color = c.accent)
                    Text("$weekBlocked tentatives bloquées", style = NdsType.bodySmall, color = c.textSecondary)
                }
            }
            item {
                GlassCard {
                    SectionLabel("ACTIVITÉ PAR HEURE")
                    // ponytail: heatmap needs a 7×24 bucketed query; band placeholder until then.
                    Box(Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(Radius.small)).background(c.accentSoft))
                }
            }
        }

        if (tab == 2) {
            item {
                GlassCard {
                    SectionLabel("TEMPS GAGNÉ CE MOIS")
                    Text(Format.duration(monthSecs), style = NdsType.displayLarge, color = c.accent)
                    Text("Temps d'écran dépensé", style = NdsType.bodySmall, color = c.textSecondary)
                    Text("vs. sans NoDoomScroll", style = NdsType.bodySmall, color = c.textTertiary)
                    Spacer(Modifier.height(Space.s3))
                    NdsProgressBar(fraction = 0f)
                    Text("Réduction ce mois", style = NdsType.labelSmall, color = c.textSecondary, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }

        if (tab == 3) {
            item {
            GlassCard(radius = Radius.cardLg) {
                SectionLabel("TEMPS GAGNÉ TOTAL · ALL-TIME")
                Box(
                    Modifier.fillMaxWidth().padding(top = Space.s3).clip(RoundedCornerShape(Radius.card))
                        .border(2.dp, c.accent, RoundedCornerShape(Radius.card))
                        .background(c.emphBg)
                        .padding(vertical = Space.s6, horizontal = Space.s4),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(Format.longDuration(allTimeSecs), style = NdsHeroNumber, color = c.accent)
                        Text("(depuis activation)", style = NdsType.bodyMedium, color = c.textSecondary)
                    }
                }
                Text("Données depuis : ${Format.date(streak?.activationMillis ?: 0L)}", style = NdsType.labelSmall, color = c.textTertiary, modifier = Modifier.padding(top = Space.s4, bottom = Space.s6))

                StatRow("Jours sans scroll consécutifs :", "${streak?.currentStreakDays ?: 0}", valueColor = c.accent, modifier = Modifier.padding(bottom = Space.s2))
                StatRow("Sessions mode libre utilisées :", "$sessions", modifier = Modifier.padding(bottom = Space.s2))
                StatRow("Taux de blocage moyen :", "$rate%")
            }
            }
        }

        item {
            GlassCard {
                SectionLabel("TENTATIVES DE CONTOURNEMENT ▼")
                Spacer(Modifier.height(Space.s3))
                bypasses.forEach { b ->
                    Text("${if (b.wasBlocked) "Bloqué" else "Passé"} · ${Format.dayTime(b.timestampMillis)}", style = NdsType.bodySmall, color = c.textSecondary, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}