package com.posyanduku.app.feature.scan

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Scan Buku KIA: pada build produksi dihubungkan ke CameraX + ML Kit OCR
 * untuk memasukkan data dari buku KIA secara otomatis. Di demo ini disediakan
 * penampung dengan panduan penggunaan (tanpa runtime crash saat kamera tak aktif).
 */
@Composable
fun ScanScreen() {
    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier.size(160.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Text("📷", style = MaterialTheme.typography.displaySmall)
        }
        Spacer(Modifier.height(24.dp))
        Text("Scan Buku KIA", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        Text(
            "Arahkan kamera ke halaman data bayi/ibu di Buku KIA, maka nama & tanggal lahir akan terisi otomatis ke formulir pendaftaran.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
        ) {
            Text(
                "Fitur pengenalan teks menyala di aplikasi produksi.\nDemo ini tampil tanpa kamera agar bisa dibuka di emulator.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.padding(16.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}
