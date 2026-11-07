package com.posyanduku.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Skala aksesibilitas. Kita gandakan ukuran teks & hit target lewat faktor
 * fontScale (1x Normal / 1.25x Besar / 1.5x Sangat Besar). Ini pilihan sadar
 * untuk kader awam & lansia: huruf dan tombol besar lebih penting dari
 * kepadatan informasi.
 */
enum class UiScale(val label: String, val fontScale: Float, val desc: String) {
    NORMAL("Normal", 1f, "Ukuran standar"),
    BESAR("Besar", 1.25f, "Huruf & tombol lebih besar"),
    SANGAT_BESAR("Sangat Besar", 1.5f, "Paling mudah dibaca"),
}

val LocalUiScale = staticCompositionLocalOf { UiScale.BESAR }

private val LightColors = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = PrimaryDark,
    onSecondary = OnPrimary,
    surface = Surface,
    background = Background,
    surfaceVariant = SurfaceVariant,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    error = Danger,
    onError = OnDanger,
    errorContainer = DangerContainer,
)

@Composable
fun PosyanduKuTheme(
    uiScale: UiScale = LocalUiScale.current,
    content: @Composable () -> Unit,
) {
    // Sengaja hanya light: pengguna target butuh kontras maksimal & konsistensi.
    val dark = isSystemInDarkTheme()
    CompositionLocalProvider(LocalUiScale provides uiScale) {
        MaterialTheme(
            colorScheme = LightColors,
            typography = posyanduTypography(uiScale.fontScale),
            content = content,
        )
    }
}
