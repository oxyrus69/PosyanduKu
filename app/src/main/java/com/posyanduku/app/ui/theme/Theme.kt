package com.posyanduku.app.ui.theme

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
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    surface = Surface,
    background = Background,
    surfaceVariant = SurfaceVariant,
    onSurface = OnSurface,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
    error = Danger,
    onError = OnDanger,
    errorContainer = DangerContainer,
    surfaceBright = Surface,
    surfaceDim = SurfaceContainer,
    surfaceContainerLowest = SurfaceContainerLowest,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainer = SurfaceContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerHighest = SurfaceContainerHighest,
    inverseSurface = OnSurface,
    inverseOnSurface = Surface,
)

@Composable
fun PosyanduKuTheme(
    uiScale: UiScale = LocalUiScale.current,
    content: @Composable () -> Unit,
) {
    // Sengaja hanya light: pengguna target butuh kontras maksimal & konsistensi.
    // enableEdgeToEdge() dari ComponentActivity mengurus warna ikon system bar
    // otomatis, jadi di sini kita tidak menyetel isAppearanceLight*Bar lagi.
    CompositionLocalProvider(LocalUiScale provides uiScale) {
        MaterialTheme(
            colorScheme = LightColors,
            typography = posyanduTypography(uiScale.fontScale),
            content = content,
        )
    }
}
