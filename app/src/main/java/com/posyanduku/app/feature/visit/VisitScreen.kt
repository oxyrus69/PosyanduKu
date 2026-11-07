package com.posyanduku.app.feature.visit

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.posyanduku.app.anthro.ZScore
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.ui.FlagPicker
import com.posyanduku.app.ui.flagColors
import com.posyanduku.app.ui.subtitle
import com.posyanduku.app.ui.umurLabel

@Composable
fun VisitScreen(vm: VisitViewModel, targetId: String, onDone: () -> Unit) {
    val ui by vm.ui.collectAsState()
    LaunchedEffect(targetId) { vm.load(targetId) }
    val t = ui.target ?: return

    Column(Modifier.fillMaxSize()) {
        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {

            Column {
                Text(t.nama, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text(subtitle(t),
                    style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            when (t.type) {
                TargetType.BALITA -> BalitaForm(ui, vm)
                TargetType.BUMIL -> BumilForm(ui, vm)
                TargetType.REMAJA_LANSIA -> RemlanForm(ui, vm)
            }

            // Status gizi langsung (balita)
            if (t.type == TargetType.BALITA && (ui.live.zBbu != null || ui.live.zBbtb != null)) {
                StatusGiziCard(ui.live)
            }

            // Petunjuk KIE
            HintCard(t.type, ui)

            // Penandaan manual 3 warna (opsional, jika kader ingin mengubah)
            Column {
                Text("Status akhir (otomatis; ubah jika perlu)",
                    style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(10.dp))
                FlagPicker(
                    selected = ui.manualFlag ?: defaultFlag(ui),
                    onPick = { vm.setFlag(if (ui.manualFlag == it) null else it) },
                )
            }

            OutlinedTextField(
                value = ui.catatan, onValueChange = vm::setCatatan,
                modifier = Modifier.fillMaxWidth().heightIn(min = 120.dp),
                label = { Text("Catatan kader (KIE, obat, dsb.)") },
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(14.dp),
            )
        }

        // Sticky CTA besar
        Button(
            onClick = { vm.simpan(onDone) },
            enabled = !ui.saving && inputsOk(ui),
            modifier = Modifier.fillMaxWidth().padding(20.dp).heightIn(min = 80.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                if (ui.saving) "Menyimpan..." else "Simpan Kunjungan",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            )
        }
    }
}

private fun inputsOk(ui: VisitUi): Boolean {
    return when (ui.target?.type) {
        TargetType.BALITA -> ui.bb.toDoubleOrNull() != null || ui.tb.toDoubleOrNull() != null
        TargetType.BUMIL -> ui.lila.toDoubleOrNull() != null || ui.bb.toDoubleOrNull() != null
        TargetType.REMAJA_LANSIA -> ui.bb.toDoubleOrNull() != null || ui.tensiSys.toIntOrNull() != null
        null -> false
    }
}

private fun defaultFlag(ui: VisitUi): StatusFlag = when (ui.target?.type) {
    TargetType.BALITA -> when (ui.live.status) {
        ZScore.StatusGizi.RUJUK -> StatusFlag.RUJUK
        ZScore.StatusGizi.PERHATIAN -> StatusFlag.PERHATIAN
        ZScore.StatusGizi.OKE -> StatusFlag.OKE
    }
    else -> StatusFlag.OKE
}

@Composable private fun BalitaForm(ui: VisitUi, vm: VisitViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        BigNum("Berat badan", "kg", ui.bb, vm::setBb, Modifier.weight(1f))
        BigNum("Tinggi/panjang", "cm", ui.tb, vm::setTb, Modifier.weight(1f))
    }
}

@Composable private fun BumilForm(ui: VisitUi, vm: VisitViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        BigNum("Berat badan", "kg", ui.bb, vm::setBb, Modifier.weight(1f))
        BigNum("LiLA", "cm", ui.lila, vm::setLila, Modifier.weight(1f))
    }
}

@Composable private fun RemlanForm(ui: VisitUi, vm: VisitViewModel) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        BigNum("Berat badan", "kg", ui.bb, vm::setBb, Modifier.weight(1f))
        BigNum("Tinggi", "cm", ui.tb, vm::setTb, Modifier.weight(1f))
    }
    Spacer(Modifier.height(12.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        BigNum("Tensi sistolik", "", ui.tensiSys, vm::setTensiSys, Modifier.weight(1f))
        BigNum("Tensi diastolik", "", ui.tensiDia, vm::setTensiDia, Modifier.weight(1f))
    }
}

@Composable
private fun BigNum(label: String, unit: String, value: String, onChange: (String) -> Unit, modifier: Modifier) {
    Column(modifier) {
        Text(label, style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(6.dp))
        OutlinedTextField(
            value = value, onValueChange = onChange,
            // Jangan pakai `modifier` di sini: modifier dari pemanggil berisi
            // weight(1f) yang hanya valid untuk Column pembungkus. Kalau
            // dipakai ulang di dalam Column, tinggi field ikut menyusut.
            modifier = Modifier.fillMaxWidth().heightIn(min = 92.dp),
            textStyle = MaterialTheme.typography.displaySmall.copy(fontFamily = androidx.compose.ui.text.font.FontFamily.Default),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            trailingIcon = { if (unit.isNotBlank()) Text(unit, style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 16.dp)) },
            shape = RoundedCornerShape(14.dp),
        )
    }
}

@Composable
private fun StatusGiziCard(live: VisitUi.Live) {
    val status = when (live.status) {
        ZScore.StatusGizi.OKE -> StatusFlag.OKE
        ZScore.StatusGizi.PERHATIAN -> StatusFlag.PERHATIAN
        ZScore.StatusGizi.RUJUK -> StatusFlag.RUJUK
    }
    val (bg, fg) = flagColors(status)
    Column(
        Modifier.fillMaxWidth().background(bg, RoundedCornerShape(16.dp)).padding(16.dp),
    ) {
        Text(live.status.label, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = fg)
        Spacer(Modifier.height(8.dp))
        Text(
            zExplanation(live),
            style = MaterialTheme.typography.bodyMedium, color = fg,
        )
    }
}

private fun zExplanation(live: VisitUi.Live): String {
    val parts = ArrayList<String>()
    fun lab(v: Double?) = v?.let { if (it < -3) "sangat di bawah jalur" else if (it < -2) "di bawah jalur" else "sesuai jalur" }
    if (live.zBbu != null) parts.add("BB/U ${lab(live.zBbu)}")
    if (live.zTbu != null) parts.add("TB/U ${lab(live.zTbu)}")
    if (live.zBbtb != null) parts.add("BB/TB ${lab(live.zBbtb)}")
    return parts.joinToString(" • ").ifBlank { "Masukkan BB & TB untuk z-score." }
}

@Composable
private fun HintCard(type: TargetType, ui: VisitUi) {
    val (teks, flag) = when (type) {
        TargetType.BALITA -> {
            val l = ui.live
            if (l.status == ZScore.StatusGizi.RUJUK)
                "Bawa ke Puskesmas hari ini juga. Beri makan padat gizi menunggu antrean rujukan." to StatusFlag.RUJUK
            else if (l.status == ZScore.StatusGizi.PERHATIAN)
                "Naikkan porsi (nasi + lauk hewani/telur), imunisasi cek, kontrol 2 minggu. Bila makin turun → rujuk." to StatusFlag.PERHATIAN
            else "Pertahankan ASI/MP-ASI & imunisasi. Timbang tiap bulan." to StatusFlag.OKE
        }
        TargetType.BUMIL -> {
            val lila = ui.lila.toDoubleOrNull()
            if (lila != null && lila < 23.5)
                "LiLA kurang dari 23,5 cm: rujuk bidan. Edukasi tambah lauk & istirahat." to StatusFlag.RUJUK
            else "ANC teratur: minimal 6× periksa selama hamil. Tablet tambah darah tiap hari." to StatusFlag.OKE
        }
        TargetType.REMAJA_LANSIA -> {
            val sys = ui.tensiSys.toIntOrNull()
            if (sys != null && sys >= 180)
                "Tekanan sangat tinggi. Istirahatkan & rujuk ke Puskesmas/dokter." to StatusFlag.RUJUK
            else if (sys != null && sys >= 140)
                "Tekanan di atas normal. Ukur ulang minggu depan, kurangi garam, jalan 30 menit/hari." to StatusFlag.PERHATIAN
            else "Jaga aktivitas fisik & cek tekanan darah tiap Posyandu." to StatusFlag.OKE
        }
    }
    val (bg, fg) = flagColors(flag)
    Column(Modifier.fillMaxWidth().background(bg, RoundedCornerShape(16.dp)).padding(16.dp)) {
        Text("Yang disarankan kader:", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = fg)
        Spacer(Modifier.height(6.dp))
        Text(teks, style = MaterialTheme.typography.bodyMedium, color = fg)
    }
}
