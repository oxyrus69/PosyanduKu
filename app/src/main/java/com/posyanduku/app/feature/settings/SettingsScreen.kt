package com.posyanduku.app.feature.settings

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
import com.posyanduku.app.ui.theme.UiScale

@Composable
fun SettingsScreen(vm: SettingsViewModel) {
    val ui by vm.ui.collectAsState()

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
    ) {
        Text("Pengaturan", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)

        // Ukuran tampilan — pemilih besar & jelas
        Column {
            Text("Ukuran huruf & tombol", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(12.dp))
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                UiScale.entries.forEach { opt ->
                    val sel = ui.scale == opt
                    Row(
                        Modifier.fillMaxWidth()
                            .background(
                                if (sel) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(14.dp),
                            )
                            .clickable { vm.setScale(opt) }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(selected = sel, onClick = { vm.setScale(opt) })
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(opt.label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(opt.desc, style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        // Nama kader
        Column {
            Text("Nama kader (muncul di sambutan)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            var tmp by remember(ui.kader) { mutableStateOf(ui.kader) }
            OutlinedTextField(
                value = tmp, onValueChange = { tmp = it; vm.setKader(it) },
                modifier = Modifier.fillMaxWidth().heightIn(min = 76.dp),
                textStyle = MaterialTheme.typography.bodyLarge,
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                placeholder = { Text("Mis. \"Wati\"") },
            )
        }

        // Kesiapan sinkron & penyimpanan lokal
        Column {
            Text("Data tersimpan di HP ini", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            InfoRow("Jumlah sasaran", "${ui.targetCount} sasaran")
            InfoRow("Belum terkirim ke server", "${ui.outboxCount} catatan")
        }

        Column {
            Text("Tentang", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(
                "Demo pencatatan Posyandu yang berfungsi tanpa sinyal. Semua nama dan data pada pratinjau ini fiktif.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.weight(1f))
        Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
    }
}
