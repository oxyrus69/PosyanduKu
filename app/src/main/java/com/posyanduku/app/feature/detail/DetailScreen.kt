package com.posyanduku.app.feature.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.ui.FlagChip
import com.posyanduku.app.ui.StatusStrip
import com.posyanduku.app.ui.flagColors
import com.posyanduku.app.ui.subtitle
import com.posyanduku.app.ui.umurLabel
import java.time.format.DateTimeFormatter
import java.util.Locale

private val fmt = DateTimeFormatter.ofPattern("d MMM yyyy", Locale("id"))

@Composable
fun DetailScreen(
    vm: DetailViewModel,
    targetId: String,
    onBack: () -> Unit,
    onVisit: (String) -> Unit,
    onSdidtk: (String) -> Unit,
) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(targetId) { vm.load(targetId) }
    val t = ui.target ?: return

    var confirmVaccine by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Column {
                Text(t.nama, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoPill(t.type.label)
                    InfoPill(subtitle(t).let { s ->
                        // Hilangkan prefiks tipe karena sudah ada pill pertama
                        s.removePrefix("${t.type.label} • ")
                    })
                    if (t.namaIbu != null) InfoPill("Ibu ${t.namaIbu}")
                }
            }
        }

        if (t.type == TargetType.BUMIL && t.hpht != null) {
            item {
                val hpl = t.hpht.plusDays(280)
                val weeks = java.time.temporal.ChronoUnit.WEEKS.between(t.hpht, java.time.LocalDate.now())
                InfoCard("Kehamilan") {
                    KeyVal("Hamil ke-", "${t.hamilKe ?: "-"}")
                    KeyVal("HPHT", t.hpht.format(fmt))
                    KeyVal("HPL (taksiran)", hpl.format(fmt))
                    KeyVal("Usia kehamilan", "${weeks} minggu")
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                if (t.type == TargetType.BALITA) {
                    OutlinedButton(
                        onClick = { onSdidtk(t.id) },
                        modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                        shape = RoundedCornerShape(14.dp),
                    ) { Text("SDIDTK", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
                }
                Button(
                    onClick = { onVisit(t.id) },
                    modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                    shape = RoundedCornerShape(14.dp),
                ) { Text("Catat Kunjungan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold) }
            }
        }

        // ---- Riwayat kunjungan ----
        item {
            Text("Riwayat kunjungan", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
        items(ui.visits, key = { it.id }) { v ->
            VisitRow(v)
        }
        if (ui.visits.isEmpty()) {
            item { Text("Belum ada kunjungan.", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant) }
        }

        // ---- Imunisasi ----
        if (ui.immunizations.isNotEmpty()) {
            item {
                Text("Jadwal imunisasi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            items(ui.immunizations, key = { it.id }) { im ->
                ImmunDetailRow(im, onGiven = { confirmVaccine = im.id })
            }
        }
    }

    // Dialog konfirmasi besar
    val pending = confirmVaccine?.let { id -> ui.immunizations.firstOrNull { it.id == id } }
    if (pending != null) {
        AlertDialog(
            onDismissRequest = { confirmVaccine = null },
            title = { Text("Tandai diberi?", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
            text = { Text("${pending.vaksin} • ${pending.dosis}\nuntuk ${t.nama} hari ini?", style = MaterialTheme.typography.bodyLarge) },
            confirmButton = {
                Button(onClick = {
                    vm.markGiven(pending) { confirmVaccine = null }
                }) { Text("Ya, sudah diberi", style = MaterialTheme.typography.labelLarge) }
            },
            dismissButton = {
                OutlinedButton(onClick = { confirmVaccine = null }) { Text("Batal", style = MaterialTheme.typography.labelLarge) }
            },
        )
    }
}


@Composable private fun InfoPill(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
    )
}

@Composable private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
            .padding(16.dp),
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer)
        Spacer(Modifier.height(12.dp))
        content()
    }
}

@Composable private fun KeyVal(k: String, v: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp)) {
        Text(k, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(0.4f, fill = false))
        Text(v, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}

@Composable private fun VisitRow(v: com.posyanduku.app.data.db.VisitEntity) {
    Row(Modifier.height(IntrinsicSize.Min)
        .fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))) {
        StatusStrip(v.status)
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(v.tanggal.format(fmt), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f))
                FlagChip(v.status, compact = true)
            }
            Spacer(Modifier.height(4.dp))
            Text(v.catatan, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(4.dp))
            val metrik = buildList<String> {
                if (v.bb != null) add("BB ${v.bb} kg")
                if (v.tb != null) add("TB ${v.tb} cm")
                if (v.lila != null) add("LiLA ${v.lila} cm")
                if (v.imt != null) add("IMT ${"%.1f".format(v.imt)}")
                if (v.zBbu != null) add("z-BB/U ${"%.1f".format(v.zBbu)}")
            }
            if (metrik.isNotEmpty()) {
                Text(metrik.joinToString(" • "), style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable private fun ImmunDetailRow(im: com.posyanduku.app.data.db.ImmunizationEntity, onGiven: () -> Unit) {
    val given = im.diberikan != null
    Row(Modifier.fillMaxWidth()
        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
        .padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text("${im.vaksin} • ${im.dosis}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            Text(
                if (given) "Diberi ${im.diberikan!!.format(fmt)}" else "Jadwal ${im.jadwal.format(fmt)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        if (given) {
            Text("SELESAI", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary)
        } else {
            Button(onClick = onGiven, shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp)) {
                Text("Tandai diberi", style = MaterialTheme.typography.labelMedium)
            }
        }
    }
}
