package com.aden.nodoomscroll.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.aden.nodoomscroll.data.BlockedApp
import com.aden.nodoomscroll.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppsScreen(vm: AppViewModel) {
    val c = LocalNdsColors.current
    val apps by vm.apps.collectAsState()
    var showAdd by remember { mutableStateOf(false) }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = Space.s4, end = Space.s4, top = Space.s6, bottom = 96.dp),
            verticalArrangement = Arrangement.spacedBy(Space.s3)
        ) {
            item {
                Text("Apps filtrées", style = NdsType.headlineLarge, color = c.textPrimary)
                Spacer(Modifier.height(Space.s2))
                Text("On bloque le contenu addictif, tu gardes le reste.", style = NdsType.bodyMedium, color = c.textSecondary)
                Spacer(Modifier.height(Space.s4))
            }
            items(apps) { app -> AppRow(app) }
        }

        // FAB — Ajouter une app
        Box(
            Modifier.align(Alignment.BottomCenter).padding(Space.s4).fillMaxWidth()
        ) {
            PrimaryButton("Ajouter une app", onClick = { showAdd = true })
        }
    }

    if (showAdd) {
        AddAppSheet(
            onDismiss = { showAdd = false },
            onConfirm = { pkg, label, grayscale ->
                vm.addCustomApp(pkg, label, grayscale)
                showAdd = false
            }
        )
    }
}

@Composable
private fun AppRow(app: BlockedApp) {
    val c = LocalNdsColors.current
    GlassCard(radius = 22.dp, padding = PaddingValues(16.dp)) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).clip(RoundedCornerShape(Radius.small)).background(c.accentSoft), contentAlignment = Alignment.Center) {
                Text(app.appLabel.take(1), style = NdsType.titleLarge, color = c.accent)
            }
            Spacer(Modifier.width(Space.s3))
            Column(Modifier.weight(1f)) {
                Text(app.appLabel, style = NdsType.labelLarge, color = c.textPrimary)
                Text(
                    if (app.blockType == BlockedApp.SELECTIVE) "Bloqué : ${app.blockedSectionsDesc}"
                    else if (app.blockType == BlockedApp.GRAYSCALE_ONLY) "Niveaux de gris uniquement"
                    else "Blocage total",
                    style = NdsType.bodySmall, color = c.textSecondary
                )
            }
            if (!app.isNative) {
                Box(Modifier.clip(RoundedCornerShape(Radius.pill)).background(c.emphBg).padding(horizontal = Space.s2, vertical = 4.dp)) {
                    Text("Perso", style = NdsType.labelSmall, color = c.textSecondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAppSheet(
    onDismiss: () -> Unit,
    onConfirm: (pkg: String, label: String, grayscaleOnly: Boolean) -> Unit,
) {
    val c = LocalNdsColors.current
    val sheetState = rememberModalBottomSheetState()
    var label by remember { mutableStateOf(TextFieldValue("")) }
    var pkg by remember { mutableStateOf(TextFieldValue("")) }
    var grayscale by remember { mutableStateOf(false) }
    var confirmStep by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState, containerColor = c.bgBase) {
        Column(Modifier.padding(horizontal = Space.s6).padding(bottom = Space.s8)) {
            if (!confirmStep) {
                Text("Ajouter une app", style = NdsType.headlineMedium, color = c.textPrimary)
                Spacer(Modifier.height(Space.s4))
                OutlinedTextField(value = label, onValueChange = { label = it }, label = { Text("Nom de l'app") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(Space.s3))
                OutlinedTextField(value = pkg, onValueChange = { pkg = it }, label = { Text("Package (ex: com.exemple.app)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(Space.s4))
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text("Niveaux de gris uniquement", style = NdsType.labelLarge, color = c.textPrimary)
                        Text("Sinon : blocage total de l'app", style = NdsType.bodySmall, color = c.textSecondary)
                    }
                    Switch(checked = grayscale, onCheckedChange = { grayscale = it }, colors = SwitchDefaults.colors(checkedTrackColor = c.accent))
                }
                Spacer(Modifier.height(Space.s6))
                PrimaryButton("Continuer", enabled = label.text.isNotBlank() && pkg.text.isNotBlank(), onClick = { confirmStep = true })
            } else {
                // Irreversibility warning
                Text("Bloquer ${label.text} entièrement ?", style = NdsType.headlineMedium, color = c.textPrimary)
                Spacer(Modifier.height(Space.s4))
                Text("C'est irréversible. Une fois ajoutée, tu ne pourras plus retirer cette app tant que NoDoomScroll est installé.", style = NdsType.bodyLarge, color = c.warn)
                Spacer(Modifier.height(Space.s6))
                PrimaryButton("Confirmer le blocage", onClick = { onConfirm(pkg.text.trim(), label.text.trim(), grayscale) })
                Spacer(Modifier.height(Space.s3))
                SecondaryButton("Annuler", onClick = onDismiss)
            }
        }
    }
}
