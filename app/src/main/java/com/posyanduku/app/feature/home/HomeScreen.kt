package com.posyanduku.app.feature.home

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
import com.posyanduku.app.feature.list.TargetCard
import com.posyanduku.app.ui.flagColors
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    vm: HomeViewModel,
    onOpenTarget: (String) -> Unit,
    onVisit: (String) -> Unit,
    onSeeAllPantau: () -> Unit,
    onRegister: () -> Unit,
    onSeeAll: () -> Unit,
) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(Unit) { vm.load() }

    val urgent = ui.sum.filter { it.flag != StatusFlag.OKE }
    val greeting = ui.kader?.let { "Kader $it" } ?: "Kader Posyandu"
    val today = java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("d MMMM", Locale("id")))

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        // ---- Sambutan singkat: siapa + tanggal, satu baris rasa hening ----
        item {
            Text(
                "$greeting • $today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // ---- Ringkasan ----
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                KpiBig("Warga", ui.sasaran.toString(), Modifier.weight(1f))
                KpiBig("KK", ui.keluarga.toString(), Modifier.weight(1f))
                KpiBig("Pantau", ui.perluTindakan.toString(), Modifier.weight(1f),
                    flag = if (ui.perluTindakan > 0) StatusFlag.PERHATIAN else StatusFlag.OKE)
            }
        }

        // ---- Jawaban satu kalimat untuk "siapa yang kupanggil hari ini" ----
        item {
            if (urgent.isEmpty()) {
                Column {
                    Text(
                        "Semua aman hari ini",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "Tidak ada yang perlu dipanggil.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                Text(
                    "Panggil ${urgent.size} warga hari ini",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }

        // ---- Hanya dua kartu yang langsung bisa ditekan; sisanya lewat tombol ----
        items(urgent.take(2), key = { it.target.id }) { s ->
            TargetCard(s, onOpen = { onOpenTarget(s.target.id) }, onVisit = { onVisit(s.target.id) })
        }
        if (urgent.isNotEmpty()) {
            item {
                OutlinedButton(
                    onClick = onSeeAllPantau,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Text(
                        if (urgent.size > 2) "Lihat ${urgent.size - 2} warga lainnya"
                        else "Lihat daftar pantau",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }

        // ---- Imunisasi jatuh tempo ----
        item {
            Text(
                "Imunisasi jatuh tempo",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
        }
        val jadwal = ui.imunisasi.take(3)
        if (jadwal.isEmpty()) {
            item {
                Text(
                    "Tidak ada jadwal 30 hari ke depan.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
    val hariMenuju = jadwal.toEpochDay() - now.toEpochDay()
    val telat = hariMenuju < 0
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
                Text("Terlambat", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
            } else if (hariMenuju == 0L) {
                Text("Hari ini", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
            } else {
                Text("$hariMenuju hari lagi", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
            }
            Text(jadwal.format(DateTimeFormatter.ofPattern("d MMM", Locale("id"))),
                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
