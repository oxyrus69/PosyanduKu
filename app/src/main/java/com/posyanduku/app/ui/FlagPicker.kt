package com.posyanduku.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.posyanduku.app.data.StatusFlag

/** Baris besar "Baik / Perlu Perhatian / Rujuk" untuk pemilih status manual. */
@Composable
fun FlagPicker(selected: StatusFlag, onPick: (StatusFlag) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
        listOf(StatusFlag.OKE, StatusFlag.PERHATIAN, StatusFlag.RUJUK).forEach { f ->
            val (bg, fg) = flagColors(f)
            val isSel = f == selected
            val bgMod = if (isSel) bg else MaterialTheme.colorScheme.surfaceVariant
            val fgMod = if (isSel) fg else MaterialTheme.colorScheme.onSurfaceVariant
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(bgMod)
                    .clickable { onPick(f) }
                    .padding(vertical = 18.dp),
            ) {
                Text(
                    flagLabel(f).uppercase(),
                    modifier = Modifier.align(androidx.compose.ui.Alignment.Center),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = fgMod,
                )
            }
        }
    }
}
