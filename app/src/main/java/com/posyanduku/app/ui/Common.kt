package com.posyanduku.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.ui.theme.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import com.posyanduku.app.data.TargetType

/** Warna latar & isi untuk StatusFlag. */
fun flagColors(flag: StatusFlag): Pair<Color, Color> = when (flag) {
    StatusFlag.OKE -> SuccessContainer to Success
    StatusFlag.PERHATIAN -> WarningContainer to Warning
    StatusFlag.RUJUK -> DangerContainer to Danger
}

fun flagLabel(flag: StatusFlag): String = when (flag) {
    StatusFlag.OKE -> "Baik"
    StatusFlag.PERHATIAN -> "Pantau"
    StatusFlag.RUJUK -> "Rujuk"
}

fun typeIcon(type: TargetType): ImageVector = when (type) {
    TargetType.BUMIL -> Icons.Filled.Favorite
    TargetType.BALITA -> Icons.Filled.Face
    TargetType.REMAJA_LANSIA -> Icons.Filled.People
}

/**
 * Bar status tebal di tepi kiri kartu — dipakai di kartu sasaran, kartu
 * "perlu tindakan", dan indikator apapun yang harus terbaca sekejap mata.
 */
@Composable
fun StatusStrip(flag: StatusFlag, modifier: Modifier = Modifier, width: Int = 8) {
    val (_, fg) = flagColors(flag)
    Box(
        modifier
            .width(width.dp)
            .fillMaxHeight()
            .background(fg)
    )
}

@Composable
fun FlagChip(flag: StatusFlag, compact: Boolean = false) {
    val (bg, fg) = flagColors(flag)
    Row(
        modifier = Modifier
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = if (compact) 8.dp else 12.dp, vertical = if (compact) 4.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            flagLabel(flag).uppercase(),
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = fg,
        )
    }
}

/** Umur manusiawi: "2 th 4 bl", "8 bl", "21 hr"; di bawah 2 tahun pakai bulan. */
fun umurLabel(birth: java.time.LocalDate, today: java.time.LocalDate = java.time.LocalDate.now()): String {
    val months = birth.until(today).toTotalMonths().toInt()
    if (months >= 24) {
        val th = months / 12; val bl = months % 12
        return if (bl == 0) "${th} tahun" else "${th} th ${bl} bl"
    }
    val days = birth.until(today).days
    if (months == 0) return "${days} hari"
    return "${months} bulan"
}

/** Subtitle pada kartu: disadur per tipe agar bahasa kader tetap awam. */
fun subtitle(t: com.posyanduku.app.data.db.TargetEntity): String = when (t.type) {
    TargetType.BUMIL ->
        if (t.hpht != null) {
            val minggu = java.time.temporal.ChronoUnit.WEEKS.between(t.hpht, java.time.LocalDate.now())
            "Hamil ${minggu} minggu • Usia ${umurTahun(t.tanggalLahir)}"
        } else "Ibu Hamil • Usia ${umurTahun(t.tanggalLahir)}"
    else -> "${t.type.label} • ${umurLabel(t.tanggalLahir)}"
}

private fun umurTahun(birth: java.time.LocalDate): String {
    val th = birth.until(java.time.LocalDate.now()).years
    return "${th} th"
}
