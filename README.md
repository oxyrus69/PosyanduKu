# PosyanduKu

Aplikasi Android **pencatatan Posyandu offline-first** untuk kader awam & usia tua — bahasa Indonesia, huruf & tombol besar, kontras tinggi, satu alur per layar. Template portofolio meniru pola aplikasi KIA berbasis FHIR (pola rujukan: OpenSRP/FHIR Core), seluruhnya sisi perangkat.

## Fitur inti

- **Beranda** — sambutan + tanggal, 3 KPI besar (Sasaran, Keluarga, Pantauan), daftar "Perlu tindakan hari ini" (dahulukan RUJUK & yang lama tak ditimbang), jadwal imunisasi mendekat.
- **Sasaran** — 20 sasaran contoh dalam 10 keluarga: Ibu Hamil (HPHT, usia hamil minggu), Balita, Remaja/Lansia; filter besar per kelompok + pencarian nama; strip warna tebal (Baik/Pantau/Rujuk).
- **Detail** — kartu kehamilan (HPHT, HPL, minggu), riwayat kunjungan (dengan metrik & z-score), jadwal imunisasi; penandaan "Tandai diberi" dengan dialog konfirmasi besar.
- **Kunjungan** — input angka super besar (BB/TB/LiLA/Tensi); z-score WHO (BB/U, TB/U, BB/TB) dihitung langsung di perangkat dari tabel LMS WHO 2006, pita hasil berubah warna & saran KIE mengikuti; status akhir otomatis (dapat diubah manual).
- **SDIDTK (KPSP)** — butir pertanyaan per rentang umur (tersederhanakan), skor 9–10 Sesuai / 7–8 Meragukan / ≤6 Penyimpangan, hasil & saran langsung, tersimpan ke riwayat.
- **Imunisasi** — kartu jadwal per sasaran + daftar jatuh tempo di Beranda.
- **Pengaturan** — pemilih skala tampilan 1× / 1.25× / 1.5× yang mengubah ukuran huruf secara global, nama kader, status outbox sinkronisasi.
- **Offline-first** — Room SQLite + **outbox idempoten** (catatan baru antri kirim; label "Menunggu dikirim ke server" di Pengaturan). Mode demo ter-seed otomatis saat pertama dibuka.

## Build

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"   # JDK 17
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
.\gradlew.bat :app:assembleDebug --no-daemon
# Output: app\build\outputs\apk\debug\app-debug.apk
```

## Stack

Kotlin 2.0 · Jetpack Compose (Material 3) · Navigation-Compose · Room + TypeConverter `LocalDate` · DataStore Preferences · AGP 8.7.2 / Gradle 8.9 · minSdk 24 · targetSdk 35.

## Catatan desain

- Satu jalur per layar; satu CTA primer lengket di bawah; dialog konfirmasi untuk aksi tidak bisa-batal.
- Hijau keperawatan + label status semantik konsisten; tidak ada gradien ungu, glow, atau stripe dekoratif.
- Skala aksesibilitas diterapkan sekali lewat `posyanduTypography(scale)` (1× / 1.25× / 1.5×). Jangan digandakan di `LocalDensity`: dulu nilainya dikali dua kali sehingga "Sangat Besar" efektif 2.25× dan label tombol terpotong.
- Tombol dan chip memakai `heightIn(min = ...)`, bukan `height(...)`, supaya tinggi ikut tumbuh saat skala diperbesar.
- Tabel LMS WHO di-embed (bukan HTTP) → z-score bekerja di mode pesawat.

*Data pada pratinjau ini fiktif; untuk penggunaan klinis sungguhan diperlukan review tenaga kesehatan & koneksi ke server FHIR resmi.*
