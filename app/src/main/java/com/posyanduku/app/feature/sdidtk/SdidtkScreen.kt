package com.posyanduku.app.feature.sdidtk

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.posyanduku.app.data.Kpsp
import com.posyanduku.app.data.KpspItem
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.ui.flagColors
import com.posyanduku.app.ui.subtitle
import com.posyanduku.app.ui.umurLabel

@Composable
fun SdidtkScreen(vm: SdidtkViewModel, targetId: String, onDone: () -> Unit) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(targetId) { vm.load(targetId) }
    val t = ui.target ?: return

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column {
                Text(t.nama, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("${subtitle(t)} • ${ui.items.size} butir KPSP",
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Text(
                "Jawab berdasarkan pengamatan langsung. Jawab 'Ya' hanya bila anak bisa melakukannya sekarang.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ui.items.forEach { item ->
                KpspCard(item, jawaban = ui.answers[item.nomor], onAnswer = { vm.answer(item.nomor, it) })
            }

            // Hasil langsung
            if (ui.allAnswered) {
                val flag = when (ui.hasil) {
                    Kpsp.Hasil.SESUAI -> StatusFlag.OKE
                    Kpsp.Hasil.MERAGUKAN -> StatusFlag.PERHATIAN
                    Kpsp.Hasil.PENYIMPANGAN -> StatusFlag.RUJUK
                }
                val (bg, fg) = flagColors(flag)
                Column(Modifier.fillMaxWidth().background(bg, RoundedCornerShape(16.dp)).padding(16.dp)) {
                    Text(ui.hasil.label, style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold, color = fg)
                    Spacer(Modifier.height(6.dp))
                    Text("Skor ${ui.skor} dari ${ui.items.size} butir.",
                        style = MaterialTheme.typography.bodyMedium, color = fg)
                    Spacer(Modifier.height(8.dp))
                    Text(ui.saran, style = MaterialTheme.typography.bodyMedium, color = fg)
                }
            }
        }

        Button(
            onClick = { vm.simpan(onDone) },
            enabled = ui.allAnswered && !ui.saving,
            modifier = Modifier.fillMaxWidth().padding(20.dp).heightIn(min = 80.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                if (ui.saving) "Menyimpan..." else "Simpan Hasil SDIDTK",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun KpspCard(item: KpspItem, jawaban: Boolean?, onAnswer: (Boolean) -> Unit) {
    Column(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${item.nomor}.",
                style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.width(36.dp),
            )
            Text(item.teks, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            listOf(true to "YA, BISA", false to "BELUM").forEach { (v, label) ->
                val sel = jawaban == v
                val bg = if (sel) {
                    if (v) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.errorContainer
                } else MaterialTheme.colorScheme.surfaceVariant
                val fg = if (sel) {
                    if (v) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.error
                } else MaterialTheme.colorScheme.onSurfaceVariant
                Box(
                    Modifier.weight(1f)
                        .background(bg, RoundedCornerShape(12.dp))
                        .clickable { onAnswer(v) }
                        .padding(vertical = 18.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
                }
            }
        }
    }
}
