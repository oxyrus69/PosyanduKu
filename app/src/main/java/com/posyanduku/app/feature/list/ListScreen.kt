package com.posyanduku.app.feature.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ListScreen(
    vm: ListViewModel,
    onOpenTarget: (String) -> Unit,
    onVisit: (String) -> Unit,
    onRegister: () -> Unit,
    initialOnlyPantau: Boolean = false,
) {
    val ui by vm.ui.collectAsState()
    // Route menentukan kondisi awal; "Perlu tindakan" hanya menyala bila
    // Beranda mengirim kita lewat tombol Lihat N warga lainnya.
    LaunchedEffect(initialOnlyPantau) { vm.setOnlyPantau(initialOnlyPantau) }

    LazyColumn(
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            TypeFilterRow(
                selected = ui.filter,
                onSelect = { vm.setFilter(it) },
                onlyPantau = ui.onlyPantau,
                onToggleOnlyPantau = { vm.setOnlyPantau(it) },
            )
        }
        item {
            OutlinedTextField(
                value = ui.query,
                onValueChange = vm::setQuery,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Cari nama warga...") },
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge,
                shape = RoundedCornerShape(14.dp),
            )
        }
        if (!ui.loading) {
            item {
                Text(
                    if (ui.onlyPantau && ui.filter == null)
                        "${ui.shown.size} sasaran • Hanya yang perlu tindakan"
                    else
                        "${ui.shown.size} sasaran",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(ui.shown, key = { it.target.id }) { s ->
            TargetCard(s, onOpen = { onOpenTarget(s.target.id) }, onVisit = { onVisit(s.target.id) })
        }
        item {
            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier.fillMaxWidth().heightIn(min = 64.dp),
                shape = RoundedCornerShape(14.dp),
            ) { Text("+ Tambah warga baru", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold) }
        }
    }
}
