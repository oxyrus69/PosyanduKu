# Recap Build — 8 November 2026

Sesi pembangunan **PosyanduKu** (aplikasi Pencatatan Posyandu offline-first untuk kader).
Dokumen ini agar sesi berikutnya langsung terarah: status, cara build, bug yang sudah
diperbaiki, dan apa yang masih menggantung.

---

## 1. Status ringkas

| Item | Status |
| --- | --- |
| Kode aplikasi | Selesai — 35 file Kotlin, 2.913 LOC, 51 file ter-track git |
| Build debug APK | **Sukses**, 17,9 MB, tanpa error |
| Jalan di emulator | **Sukses** — `Medium_Phone_API_37.0`, nol crash (`logcat AndroidRuntime:E` kosong) |
| Audit keterbacaan skala 1,5× | **Lulus** semua 8 layar, nol teks terpotong |
| Commit lokal | `a267a8b` di branch `main`, working tree bersih |
| **Push ke GitHub** | **BELUM** — kena `403` (penyebab & opsi di bagian 6) |
| `.gitignore` | Sudah lengkap, nol artefak build ter-stage |

---

## 2. Identitas & stack

- Nama produk: **PosyanduKu** (identitas fiktif untuk portofolio)
- Package: `com.posyanduku.app` (varian debug: `com.posyanduku.app.debug`)
- Lokasi: `C:\design Project\Android\PosyanduKu`
- Git: `user.name=fla1r`, `user.email=endriojn@gmail.com` (lokal repo, sama dengan global)
- Remote: `https://github.com/oxyrus69/PosyanduKu.git`

Stack:

- Kotlin 2.0.21, Jetpack Compose (BOM 2024.10.01), Material 3
- AGP 8.7.2, Gradle 8.9, compileSdk 35, minSdk 24, targetSdk 35
- Navigation-Compose 2.8.4
- Room 2.6.1 (SQLite) + TypeConverter `LocalDate`
- DataStore Preferences 1.1.1
- Tanpa Hilt — singleton sederhana di `PosyanduApp.kt`
- Room `exportSchema = false` karena path project ada spasi ("C:\design Project\…") yang
  merusak opsi KSP kalau skema di-export

JDK wajib: **Microsoft OpenJDK 17** (`C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot`).
JBR bawaan Android Studio adalah OpenJDK 25 dan tidak kompatibel dengan AGP 8.7.2.

---

## 3. Cara build (headless, tanpa Android Studio)

```powershell
$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.21.21-hotspot"
$env:ANDROID_HOME = "$env:LOCALAPPDATA\Android\Sdk"
$env:ANDROID_SDK_ROOT = $env:ANDROID_HOME
cd "C:\design Project\Android\PosyanduKu"
.\gradlew.bat :app:assembleDebug --no-daemon
```

Output: `app\build\outputs\apk\debug\app-debug.apk` (17,9 MB, di-gitignore).

---

## 4. Isi aplikasi

**8 layar + navigasi** (`RootNav.kt`): `home`, `list`, `scan`, `settings` (bottom nav),
plus `register`, `detail/{id}`, `visit/{id}`, `sdidtk/{id}`.
Label bottom nav: **Utama / Warga / Pindai / Atur**.

| Fitur | Isi |
| --- | --- |
| Beranda | KPI (Warga / KK / Pantau), "Perlu tindakan hari ini", jadwal imunisasi mendekat |
| Warga | 20 sasaran dalam 10 keluarga; filter per kelompok + pencarian; strip warna Baik/Pantau/Rujuk |
| Detail | Kartu kehamilan (HPHT, HPL, minggu), riwayat kunjungan + z-score, jadwal imunisasi, "Tandai diberi" |
| Kunjungan | Input angka besar (BB/TB/LiLA/Tensi), **z-score WHO dihitung langsung di perangkat** dari tabel LMS WHO 2006, pita hasil + saran KIE |
| SDIDTK (KPSP) | Butir per rentang umur, skor 9–10 Sesuai / 7–8 Meragukan / ≤6 Penyimpangan |
| Imunisasi | Kartu jadwal per sasaran + daftar jatuh tempo di Beranda |
| Pengaturan | Pemilih skala 1× / 1,25× / 1,5×, nama kader, status outbox |
| Pindai | Placeholder panduan (kamera/ML Kit sengaja tidak diaktifkan supaya build demo stabil) |

Data layer: `FamilyEntity`, `TargetEntity`, `VisitEntity`, `ImmunizationEntity`,
`SdidtkEntity`, `OutboxEntity` — 10 keluarga, 20 sasaran, 5 kunjungan seed.
Seed hanya jalan di DB baru: `adb shell pm clear com.posyanduku.app.debug` setelah
mengubah `SampleData.kt`.

Offline-first murni: Room + **outbox idempoten** (`client_request_id` UUID).
Backend FHIR sengaja tidak dihubungkan; layar Pindai hanya placeholder.

---

## 5. Bug yang ditemukan & diperbaiki (empat, semuanya sudah di-commit)

1. **Skala dihitung dua kali.** `Type.kt` sudah mengalikan ukuran font dasar dengan skala,
   lalu `MainActivity` *juga* mengalikan `fontScale`. Akibatnya "Sangat Besar" efektif 2,25×
   dan label tombol terpotong. Perbaikan: `MainActivity` sekarang memasok `LocalDensity`
   apa adanya; skala hanya diterapkan **sekali** lewat `posyanduTypography(scale)`.
   README sudah dikoreksi (dulu masih mengklaim skala menggandakan density).

2. **Tinggi tombol/chip tetap (`.height()`)** sehingga teks terpotong saat font membesar.
   17 lokasi diubah ke `.heightIn(min = …)` — Detail, List, Register, Visit, SDIDTK, Home, Cards.

3. **`weight()` ganda di `BigNum` (VisitScreen).** Modifier pemanggil berisi `weight(1f)`
   dipakai ulang pada `OutlinedTextField` di dalam Column, tinggi field menyusut dan label
   menimpa border. Modifier dalam kini `fillMaxWidth().heightIn(min = 92.dp)`.

4. **Umur seed tidak masuk akal.** `BUMIL/BALITA/REMAJA_LANSIA` memakai `day(n*365)` /
   `day(n*30)`, jadi ibu hamil tampil "Hamil 36 minggu • Usia 7 th". Ditambah helper
   `years(n)` dan `months(n)`; umur kini realistis (BUMIL 26–33 th, balita "14 bulan" /
   "2 th 6 bl" / "5 tahun").

Plus penyesuaian keterbacaan: KPI dipendekkan jadi "Warga / KK / Pantau", label bottom nav
`softWrap = false`, kartu BUMIL menampilkan "Hamil X minggu • Usia Y th", `umurLabel`
memakai bulan di bawah 24 bulan, baris filter chip bisa di-scroll horizontal, dan ditambah
tombol **"Lihat N warga lainnya"** di Beranda (dulu hanya 3 kartu tanpa jalan ke sisanya).

---

## 6. Verifikasi yang sudah dilakukan

- `.\gradlew.bat :app:assembleDebug --no-daemon` → sukses, APK 17,9 MB.
- Emulator `Medium_Phone_API_37.0` (1080×2400, density 420): install + buka semua layar,
  `logcat AndroidRuntime:E` kosong — nol crash.
- **Audit skala 1,5×** lewat `adb shell uiautomator dump` lalu parsing `text` + `bounds`
  untuk mendeteksi teks terpotong/tumpang tindih. Screenshot saja menyesatkan di percobaan
  pertama; metode bounds yang akurat. Hasil: Home, Detail, Visit (z-score live terverifikasi:
  10,4 kg / 92 cm → "Perlu Perhatian"), SDIDTK (butir KPSP), List/Warga (20 sasaran),
  Register, Pindai, Pengaturan — semua bersih.
- Kontras: tema terang saja (dark mode sengaja dibuang demi kontras maksimal untuk lansia).
- Emulator & adb sudah dimatikan dengan benar (pohon proses, bukan satu PID).
  Port 20128 (gateway 9router) + penjaga `9router-awasi` **tidak disentuh**.

---

## 7. Yang masih menggantung: PUSH 403

Commit `a267a8b` ada di lokal, `git push -u origin main` **gagal** dengan:

```
remote: Permission to oxyrus69/PosyanduKu.git denied to oxyrus69
```

Dua penyebab yang sudah dipastikan:

1. Fine-grained PAT yang diberikan pengguna terautentikasi sebagai `oxyrus69` dan API
   melaporkan `push=True, admin=True`, **tapi tanpa izin Contents**:
   `GET /repos/oxyrus69/PosyanduKu/contents/README.md` → 404,
   `GET /repos/oxyrus69/PosyanduKu/git/refs` → 409. Tanpa Contents, push HTTPS ditolak 403.
2. Git di mesin ini tidak lewat OpenChamber: `credential.helper=manager` (Git Credential
   Manager) tapi `~/.gcm/gcm.store` tidak ada. Endpoint kredensial OpenChamber
   (`http://127.0.0.1:57123/api/git/repository-credential`) mengembalikan `mode: none` untuk
   repo ini, dan `C:\design Project\Android\PosyanduKu` **belum terdaftar** di daftar
   `projects` pada `settings.json` OpenChamber.

OpenChamber sendiri punya token GitHub OAuth yang valid (`~/.config/openchamber/github-auth.json`,
login `oxyrus69`/`fla1r`, scope `read:org,read:user,repo,user:email,workflow`, status `valid`) —
lebih cocok dipakai daripada PAT.

**Opsi yang sudah ditawarkan ke pengguna, masih menunggu pilihan:**

- **A (disarankan):** daftarkan folder project ke OpenChamber sebagai project, lalu push
  memakai kredensial OAuth OpenChamber itu.
- **B:** pasang OpenChamber sebagai git credential helper global (lebih berisiko salah tulis).

Belum ada `gh` CLI terpasang di mesin ini.

---

## 8. Dua pertanyaan terbuka (belum dijawab pengguna)

1. Beranda dengan 15 kartu "Perlu tindakan hari ini" jadi panjang saat di-scroll — pertahankan
   atau pangkas tampilan awal?
2. Empat kalimat peringatan bertumpuk di Beranda — digabung atau dibiarkan?

---

## 9. Temuan teknis mesin ini yang perlu diingat

- JDK 17 Microsoft wajib; JBR Android Studio (OpenJDK 25) tidak jalan dengan AGP 8.7.2.
- Path project mengandung spasi → Room `exportSchema = false`.
- Uji UI yang benar: `uiautomator dump` + parsing `bounds`, bukan screenshot.
- Jangan menyapu semua `node.exe`; port 20128 & `9router-awasi` off-limits.
- Matikan emulator/adb/dev server setelah selesai; bunuh pohon proses, bukan PID tunggal.

---

## 10. Langkah berikutnya untuk sesi depan

1. Selesaikan push `a267a8b` (butuh keputusan opsi A/B di bagian 7, atau PAT baru dengan
   izin **Contents**).
2. Jawab dua pertanyaan keterbacaan di bagian 8.
3. Kalau mau lanjut fitur: kamera + ML Kit untuk Pindai/KIA book OCR, dan lapisan sync FHIR
   di atas outbox yang sudah ada (pattern idempoten sudah siap).

*Data pada aplikasi ini fiktif; untuk pemakaian klinis sungguhan perlu review tenaga
kesehatan dan koneksi ke server FHIR resmi.*
