package com.posyanduku.app.feature.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetSummary
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.data.db.TargetEntity
import com.posyanduku.app.ui.FlagChip
import com.posyanduku.app.ui.StatusStrip
import com.posyanduku.app.ui.subtitle
import com.posyanduku.app.ui.theme.Warning
import com.posyanduku.app.ui.typeIcon
import com.posyanduku.app.ui.umurLabel

/** Kartu sasaran yang dipakai di Beranda dan Daftar. */
@Composable
fun TargetCard(
    summary: TargetSummary,
    onOpen: () -> Unit,
    onVisit: () -> Unit,
) {
    val t = summary.target
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
    ) {
        Row(Modifier.height(IntrinsicSize.Min)) {
            StatusStrip(summary.flag)
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Text(t.nama, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(2.dp))
                        Text(subtitle(t), style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        if (t.type == TargetType.BALITA && t.namaIbu != null) {
                            Text("Ibu ${t.namaIbu}", style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    Spacer(Modifier.width(8.dp))
                    FlagChip(summary.flag, compact = true)
                }
                Spacer(Modifier.height(10.dp))
                Text(summary.alasan, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onVisit,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                ) {
                    Text("Catat Kunjungan", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/** Filter chip besar pemilih kelompok sasaran (digulir horizontal). */
@Composable
fun TypeFilterRow(
    selected: TargetType?,
    onSelect: (TargetType?) -> Unit,
    onlyPantau: Boolean = false,
    onToggleOnlyPantau: ((Boolean) -> Unit)? = null,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
    ) {
        FilterChip(
            label = "Semua", selected = selected == null && !onlyPantau,
            onClick = { onSelect(null); onToggleOnlyPantau?.invoke(false) },
        )
        if (onToggleOnlyPantau != null) {
            FilterChip(
                label = "Perlu tindakan",
                selected = onlyPantau,
                selectedColor = Warning,
                onClick = { onSelect(null); onToggleOnlyPantau.invoke(!onlyPantau) },
            )
        }
        TargetType.entries.sortedBy { it.urutan }.forEach { t ->
            FilterChip(t.label, selected = selected == t && !onlyPantau, onClick = { onSelect(t); onToggleOnlyPantau?.invoke(false) })
        }
    }
}

@Composable
private fun FilterChip(
    label: String, selected: Boolean, onClick: () -> Unit,
    selectedColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
) {
    val bg = if (selected) selectedColor else MaterialTheme.colorScheme.surfaceVariant
    val fg = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
    Box(
        Modifier
            .heightIn(min = 52.dp)
            .background(bg, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = fg)
    }
}
