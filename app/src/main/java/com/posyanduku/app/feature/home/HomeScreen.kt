package com.posyanduku.app.feature.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.posyanduku.app.feature.list.TargetCard
import com.posyanduku.app.ui.StatusStrip
import com.posyanduku.app.ui.flagColors
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onOpenTarget: (String) -> Unit,
    onVisit: (String) -> Unit,
    onSeeAll: () -> Unit,
    onRegister: () -> Unit,
) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.load() }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        item {
            Column {
                Text("Assalamu'alaikum,", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    ui.kader?.let { "Kader $it" } ?: "Kader Posyandu",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale("id"))),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        // ---- Ringkasan besar ----
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                KpiBig("Warga", ui.sasaran.toString(), Modifier.weight(1f))
                KpiBig("KK", ui.keluarga.toString(), Modifier.weight(1f))
                KpiBig("Pantau", ui.perluTindakan.toString(), Modifier.weight(1f),
                    flag = if (ui.perluTindakan > 0) StatusFlag.PERHATIAN else StatusFlag.OKE)
            }
        }

        // ---- Perlu tindakan hari ini ----
        item {
            val urgent = ui.sum.filter { it.flag != StatusFlag.OKE }
            Column {
                Text(
                    if (urgent.isEmpty()) "Semua terkendali hari ini"
                    else "Perlu tindakan hari ini (${urgent.size})",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                if (urgent.isNotEmpty()) {
                    Spacer(Modifier.height(4.dp))
                    Text("Dahulukan yang 'Rujuk' & yang sudah lama tak ditimbang.",
                        style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        val urgentList = ui.sum.filter { it.flag != StatusFlag.OKE }
        items(urgentList.take(3), key = { it.target.id }) { s ->
            TargetCard(s, onOpen = { onOpenTarget(s.target.id) }, onVisit = { onVisit(s.target.id) })
        }
        if (urgentList.size > 3) {
            item {
                OutlinedButton(
                    onClick = onSeeAll,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        "Lihat ${urgentList.size - 3} warga lainnya",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // ---- Jadwal imunisasi dekat ----
        item {
            Column {
                Text("Jadwal imunisasi mendekat", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text(
                    "Yang jatuh tempo dalam 30 hari. Bawa ke Posyandu bila belum diberi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        val jadwal = ui.imunisasi.take(4)
        if (jadwal.isEmpty()) {
            item {
                Text("Tidak ada jadwal dekat.", style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(jadwal, key = { it.second.id }) { (t, im) ->
                ImmunRow(t.nama, "${im.vaksin} • ${im.dosis}", im.jadwal)
            }
        }

        // ---- Aksi cepat ----
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = onSeeAll,
                    modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("Semua", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold)
                }
                Button(
                    onClick = onRegister,
                    modifier = Modifier.weight(1f).heightIn(min = 72.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text("+ Warga Baru", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun KpiBig(label: String, value: String, modifier: Modifier = Modifier, flag: StatusFlag = StatusFlag.OKE) {
    val (bg, fg) = flagColors(flag)
    val useBg = if (flag == StatusFlag.OKE) MaterialTheme.colorScheme.primaryContainer else bg
    val useFg = if (flag == StatusFlag.OKE) MaterialTheme.colorScheme.onPrimaryContainer else fg
    Column(
        modifier
            .background(useBg, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 18.dp),
    ) {
        Text(value, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold, color = useFg)
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = useFg,
            maxLines = 2,
        )
    }
}

@Composable
private fun ImmunRow(nama: String, vaksin: String, jadwal: java.time.LocalDate) {
    val now = java.time.LocalDate.now()
    val lain = jadwal.until(now).days
    val telat = jadwal.isBefore(now)
    val (bg, fg) = if (telat) flagColors(StatusFlag.PERHATIAN) else flagColors(StatusFlag.OKE)
    Row(
        Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(nama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(vaksin, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            if (telat) {
                Text("LEWAT ${-lain} HR", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
            } else {
                val d = jadwal.until(java.time.LocalDate.now()).days
                Text("${-d} hari lagi", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
            }
            Text(jadwal.format(DateTimeFormatter.ofPattern("d MMM", Locale("id"))),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
