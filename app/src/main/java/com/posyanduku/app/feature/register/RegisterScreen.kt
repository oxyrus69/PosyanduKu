package com.posyanduku.app.feature.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.posyanduku.app.data.Sex
import com.posyanduku.app.data.TargetType

@Composable
fun RegisterScreen(vm: RegisterViewModel, onDone: () -> Unit) {
    val ui by vm.ui.collectAsState()

    Column(Modifier.fillMaxSize()) {
        Column(
            Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            // Pilih kelompok sasaran
            Text("Kelompok sasaran", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TargetType.entries.sortedBy { it.urutan }.forEach { t ->
                    val sel = ui.type == t
                    FilterChip(
                        selected = sel, onClick = { vm.setType(t) },
                        label = { Text(t.label, style = MaterialTheme.typography.labelLarge) },
                        modifier = Modifier.heightIn(min = 52.dp),
                    )
                }
            }

            Field("Nama lengkap", ui.nama, vm::setNama)

            // Jenis kelamin
            Text("Jenis kelamin", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SexChip("Perempuan", ui.sex == Sex.P) { vm.setSex(Sex.P) }
                SexChip("Laki-laki", ui.sex == Sex.L) { vm.setSex(Sex.L) }
            }

            // Tanggal lahir / usia
            Text("Tanggal lahir (atau usia)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("Tgl", ui.tanggal, vm::setTanggal, Modifier.weight(1f), numeric = true)
                Field("Bln", ui.bulan, vm::setBulan, Modifier.weight(1f), numeric = true)
                Field("Thn", ui.tahun, vm::setTahun, Modifier.weight(1.6f), numeric = true)
            }
            Text("atau isi usia:", style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Field("Usia (th)", ui.umurTahun, vm::setUmurTahun, Modifier.weight(1f), numeric = true)
                Field("Usia (bl)", ui.umurBulan, vm::setUmurBulan, Modifier.weight(1f), numeric = true)
            }

            if (ui.type == TargetType.BUMIL) {
                Text("HPHT (hari pertama haid terakhir)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field("Tgl", ui.hphtTanggal, vm::setHphtTanggal, Modifier.weight(1f), numeric = true)
                    Field("Bln", ui.hphtBulan, vm::setHphtBulan, Modifier.weight(1f), numeric = true)
                    Field("Thn", ui.hphtTahun, vm::setHphtTahun, Modifier.weight(1.6f), numeric = true)
                }
                Field("Hamil ke-", ui.hamilKe, vm::setHamilKe, numeric = true)
            }

            if (ui.type == TargetType.BALITA) {
                Field("Nama ibu (opsional)", ui.namaIbu, vm::setNamaIbu)
            }

            // Keluarga
            Text("Keluarga / RT-RW", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SexChip("Pilih keluarga", !ui.useNewFamily) { vm.setUseNew(false) }
                SexChip("Keluarga baru", ui.useNewFamily) { vm.setUseNew(true) }
            }
            if (ui.useNewFamily) {
                Field("No. KK", ui.newNoKk, vm::setNewNoKk, numeric = true)
                Field("Kepala keluarga", ui.newKepala, vm::setNewKepala)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field("RT", ui.newRt, vm::setNewRt, Modifier.weight(1f), numeric = true)
                    Field("RW", ui.newRw, vm::setNewRw, Modifier.weight(1f), numeric = true)
                }
            } else {
                var expanded by remember { mutableStateOf(false) }
                val chosen = ui.families.firstOrNull { it.id == ui.familyId }
                OutlinedButton(
                    onClick = { expanded = true },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                    shape = RoundedCornerShape(14.dp),
                ) {
                    Text(
                        chosen?.let { "${it.kepala}  —  RT ${it.rt}/RW ${it.rw}" } ?: "Pilih keluarga...",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    ui.families.forEach { f ->
                        DropdownMenuItem(
                            text = { Text("${f.kepala}  —  RT ${f.rt}/RW ${f.rw}", style = MaterialTheme.typography.bodyLarge) },
                            onClick = { vm.setFamily(f.id); expanded = false },
                        )
                    }
                }
            }
        }

        Button(
            onClick = { vm.simpan(onDone) },
            enabled = ui.valid && !ui.saving,
            modifier = Modifier.fillMaxWidth().padding(20.dp).heightIn(min = 80.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Text(
                if (ui.saving) "Menyimpan..." else "Simpan Warga Baru",
                style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun Field(
    label: String, value: String, onChange: (String) -> Unit,
    modifier: Modifier = Modifier, numeric: Boolean = false,
) {
    OutlinedTextField(
        value = value, onValueChange = onChange,
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        modifier = modifier.fillMaxWidth().heightIn(min = 76.dp),
        textStyle = MaterialTheme.typography.bodyLarge,
        singleLine = true,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        shape = RoundedCornerShape(14.dp),
    )
}

@Composable
private fun SexChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected, onClick = onClick,
        label = { Text(label, style = MaterialTheme.typography.labelLarge) },
        modifier = Modifier.heightIn(min = 52.dp),
    )
}
