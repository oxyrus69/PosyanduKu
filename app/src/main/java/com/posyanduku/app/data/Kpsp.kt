package com.posyanduku.app.data

/** Satu butir pertanyaan KPSP (SDIDTK). */
data class KpspItem(val nomor: Int, val umurMaksBulan: Int, val teks: String)

/**
 * KPSP 10 butir per rentang umur (tersederhanakan dari KPSP resmi Depkes).
 * Kader menjawab Ya/Tidak; skor = jumlah Ya. 9-10 Sesuai, 7-8 Meragukan, <=6 Penyimpangan.
 */
object Kpsp {

    fun rentangUntuk(umurBulan: Int): IntRange = when {
        umurBulan < 3 -> 0..2
        umurBulan < 6 -> 3..5
        umurBulan < 9 -> 6..8
        umurBulan < 12 -> 9..11
        umurBulan < 15 -> 12..14
        umurBulan < 18 -> 15..17
        umurBulan < 21 -> 18..20
        umurBulan < 24 -> 21..23
        umurBulan < 30 -> 24..29
        umurBulan < 36 -> 30..35
        umurBulan < 42 -> 36..41
        umurBulan < 48 -> 42..47
        umurBulan < 54 -> 48..53
        umurBulan < 60 -> 54..59
        umurBulan < 66 -> 60..65
        else -> 66..71
    }

    fun butirUntuk(umurBulan: Int): List<KpspItem> = rentangKeButir[rentangUntuk(umurBulan).first]
        ?: butirDefault

    enum class Hasil(val label: String, val flag: StatusFlag) {
        SESUAI("Sesuai usia", StatusFlag.OKE),
        MERAGUKAN("Meragukan", StatusFlag.PERHATIAN),
        PENYIMPANGAN("Kemungkinan penyimpangan", StatusFlag.RUJUK),
    }

    fun skorKeHasil(skor: Int): Hasil = when {
        skor >= 9 -> Hasil.SESUAI
        skor >= 7 -> Hasil.MERAGUKAN
        else -> Hasil.PENYIMPANGAN
    }

    private val rentangKeButir: Map<Int, List<KpspItem>> = mapOf(
        0 to listOf(
            KpspItem(1, 2, "Saat ditidurkan telentang, masing-masing lengan & tungkai bergerak aktif?"),
            KpspItem(2, 2, "Bersuara oooh... / aaah... (mengoceh spontan)?"),
            KpspItem(3, 2, "Tersenyum saat diajak bicara / tersenyum sendiri?"),
            KpspItem(4, 2, "Mengedipkan mata saat ada cahaya mendadak?"),
            KpspItem(5, 2, "Menoleh ke arah bunyi-bunyian?"),
            KpspItem(6, 2, "Melihat & mengikuti wajah dengan mata?"),
            KpspItem(7, 2, "Mengangkat kepala saat telungkup (sebentar)?"),
            KpspItem(8, 2, "Meredakan diri saat digendong/ditimang?"),
            KpspItem(9, 2, "Mengisap puting dengan baik?"),
            KpspItem(10, 2, "Merespons saat diberi mainan berbunyi di dekat telinga?"),
        ),
        3 to listOf(
            KpspItem(1, 5, "Saat telungkup, mengangkat kepala 45°?"),
            KpspItem(2, 5, "Saat telungkup, mengangkat kepala 90°?"),
            KpspItem(3, 5, "Tertawa keras saat digelitik/diajak main?"),
            KpspItem(4, 5, "Bersuara vokal & konsonan (a-u, ga)?"),
            KpspItem(5, 5, "Memalingkan kepala ke suara?"),
            KpspItem(6, 5, "Menatap & meraih mainan yang digerakkan?"),
            KpspItem(7, 5, "Menggenggam benda kecil yang diletakkan di tangan?"),
            KpspItem(8, 5, "Mengenali pengasuhnya (beda reaksi ke orang lain)?"),
            KpspItem(9, 5, "Berguling telungkup → telentang?"),
            KpspItem(10, 5, "Menahan kepala tegak stabil saat digendong?"),
        ),
        6 to listOf(
            KpspItem(1, 8, "Duduk sendiri sebentar tanpa dipegangi?"),
            KpspItem(2, 8, "Duduk sambil menahan badan dengan tangan?"),
            KpspItem(3, 8, "Meraih mainan dengan satu tangan?"),
            KpspItem(4, 8, "Memindahkan benda dari satu tangan ke tangan lain?"),
            KpspItem(5, 8, "Mengoceh 'da-da', 'ba-ba' tanpa arti?"),
            KpspItem(6, 8, "Merangkak (mungkin maju atau mundur)?"),
            KpspItem(7, 8, "Tersenyum & berceloteh pada bayangan di cermin?"),
            KpspItem(8, 8, "Mencari benda yang jatuh/disembunyikan?"),
            KpspItem(9, 8, "Berusaha berdiri berpegangan?"),
            KpspItem(10, 8, "Mengulangi bunyi yang menarik perhatian?"),
        ),
        9 to listOf(
            KpspItem(1, 11, "Berdiri berpegangan pada meja/kursi?"),
            KpspItem(2, 11, "Mengangkat benda dengan ibu jari & jari lain (mencubit)?"),
            KpspItem(3, 11, "Bermain 'cilukba'?"),
            KpspItem(4, 11, "Meniru bunyi sederhana?"),
            KpspItem(5, 11, "Berjalan berpegangan satu tangan (dititah)?"),
            KpspItem(6, 11, "Mengatakan 'mama'/'papa' bermakna?"),
            KpspItem(7, 11, "Menunjuk benda yang diinginkan?"),
            KpspItem(8, 11, "Minum dari cangkir dipegang sendiri?"),
            KpspItem(9, 11, "Takut/malu pada orang asing?"),
            KpspItem(10, 11, "Menyusun/menjatuhkan kubus mainan?"),
        ),
        12 to listOf(
            KpspItem(1, 14, "Berjalan sendiri tanpa berpegangan beberapa langkah?"),
            KpspItem(2, 14, "Menumpuk 2 kubus?"),
            KpspItem(3, 14, "Mengatakan 3 kata bermakna?"),
            KpspItem(4, 14, "Menunjuk bagian tubuh saat diminta?"),
            KpspItem(5, 14, "Mengulurkan mainan kepada orang lain?"),
            KpspItem(6, 14, "Minum dari gelas tanpa banyak tumpah?"),
            KpspItem(7, 14, "Membantu memakai/melepas baju sendiri?"),
            KpspItem(8, 14, "Menirukan pekerjaan rumah sederhana?"),
            KpspItem(9, 14, "Mencoret-coret spontan?"),
            KpspItem(10, 14, "Menggeleng jika tidak mau, angguk jika mau?"),
        ),
        15 to listOf(
            KpspItem(1, 17, "Berjalan lancar & bisa jongkok-berdiri?"),
            KpspItem(2, 17, "Menumpuk 3 kubus?"),
            KpspItem(3, 17, "Mengatakan 4-5 kata bermakna?"),
            KpspItem(4, 17, "Bermain pura-pura (mis. minum dari gelas kosong)?"),
            KpspItem(5, 17, "Menunjuk gambar saat diberi nama?"),
            KpspItem(6, 17, "Melepas sepatu sendiri?"),
            KpspItem(7, 17, "Membuang sampah ke tempatnya?"),
            KpspItem(8, 17, "Lari dengan seimbang?"),
            KpspItem(9, 17, "Mengikuti perintah 1 langkah?"),
            KpspItem(10, 17, "Memutar gagang pintu?"),
        ),
        18 to listOf(
            KpspItem(1, 20, "Berjalan mundur 5 langkah?"),
            KpspItem(2, 20, "Menendang bola tanpa berpegangan?"),
            KpspItem(3, 20, "Menumpuk 4-6 kubus?"),
            KpspItem(4, 20, "Menyebutkan 6+ kata?"),
            KpspItem(5, 20, "Menyusun 2 kata (mis. 'minta minum')?"),
            KpspItem(6, 20, "Melepas baju sendiri?"),
            KpspItem(7, 20, "Makan sendiri dengan sedikit tumpah?"),
            KpspItem(8, 20, "Mencoret garis lurus meniru?"),
            KpspItem(9, 20, "Menarik baju saat mau buang air?"),
            KpspItem(10, 20, "Membungkuk memungut benda tanpa jatuh?"),
        ),
        21 to listOf(
            KpspItem(1, 23, "Naik tangga berpegangan satu tangan, 2 kaki per anak tangga?"),
            KpspItem(2, 23, "Menyusun 2-3 kata jadi kalimat?"),
            KpspItem(3, 23, "Menyebut nama diri sendiri?"),
            KpspItem(4, 23, "Membalik halaman buku satu-satu?"),
            KpspItem(5, 23, "Menumpuk 6 kubus?"),
            KpspItem(6, 23, "Mengikuti perintah 2 langkah?"),
            KpspItem(7, 23, "Bermain dengan anak lain berdampingan?"),
            KpspItem(8, 23, "Melepas pakaian sendiri?"),
            KpspItem(9, 23, "Memutar tombol/keran?"),
            KpspItem(10, 23, "Memberi tahu saat basah/ngompol?"),
        ),
        24 to listOf(
            KpspItem(1, 29, "Melompat dengan 2 kaki di tempat?"),
            KpspItem(2, 29, "Menumpuk 7-8 kubus?"),
            KpspItem(3, 29, "Mencoret lingkaran meniru?"),
            KpspItem(4, 29, "Menyebut 1 warna?"),
            KpspItem(5, 29, "Menyebut lawan kata sederhana (besar-kecil)?"),
            KpspItem(6, 29, "Memakai celana sendiri?"),
            KpspItem(7, 29, "Buang air besar/kecil di WC?"),
            KpspItem(8, 29, "Bercerita pengalaman sederhana?"),
            KpspItem(9, 29, "Bermain peran (mis. jadi ibu/ayah)?"),
            KpspItem(10, 29, "Mengerti konsep '1' dan 'banyak'?"),
        ),
        30 to listOf(
            KpspItem(1, 35, "Berdiri 1 kaki 1-2 detik?"),
            KpspItem(2, 35, "Menggambar garis vertikal & horizontal meniru?"),
            KpspItem(3, 35, "Menyebut jenis kelamin & nama lengkap?"),
            KpspItem(4, 35, "Menyusun kalimat 3-4 kata?"),
            KpspItem(5, 35, "Menumpuk 9 kubus?"),
            KpspItem(6, 35, "Menyebut 2 warna?"),
            KpspItem(7, 35, "Memakai kaos sendiri?"),
            KpspItem(8, 35, "Mencuci tangan & mengeringkan sendiri?"),
            KpspItem(9, 35, "Menggunting kertas (dengan pengawasan)?"),
            KpspItem(10, 35, "Mengerti konsep '2'?"),
        ),
        36 to listOf(
            KpspItem(1, 41, "Berdiri 1 kaki 3-5 detik?"),
            KpspItem(2, 41, "Menggambar lingkaran meniru?"),
            KpspItem(3, 41, "Menumpuk 10 kubus?"),
            KpspItem(4, 41, "Menyebut nama lengkap & umur?"),
            KpspItem(5, 41, "Mengerti konsep '3'?"),
            KpspItem(6, 41, "Bercerita runtutan kejadian sederhana?"),
            KpspItem(7, 41, "Memakai baju kancing sendiri?"),
            KpspItem(8, 41, "Sikat gigi dibantu sedikit?"),
            KpspItem(9, 41, "Naik tangga berganti kaki?"),
            KpspItem(10, 41, "Bermain bergiliran dengan anak lain?"),
        ),
        42 to listOf(
            KpspItem(1, 47, "Melompat jauh dengan 2 kaki?"),
            KpspItem(2, 47, "Menggambar tanda silang meniru?"),
            KpspItem(3, 47, "Menyebut nama jalan/rumah?"),
            KpspItem(4, 47, "Membedakan pagi/siang/malam?"),
            KpspItem(5, 47, "Menyebut 4 warna?"),
            KpspItem(6, 47, "Menghitung benda sampai 4?"),
            KpspItem(7, 47, "Memakai sepatu sendiri?"),
            KpspItem(8, 47, "Membersihkan hidung sendiri?"),
            KpspItem(9, 47, "Bermain permainan dengan aturan (kejar-kejaran)?"),
            KpspItem(10, 47, "Menirukan kalimat 6-7 kata?"),
        ),
        48 to listOf(
            KpspItem(1, 53, "Berdiri 1 kaki 5-10 detik?"),
            KpspItem(2, 53, "Menggambar bujur sangkar meniru?"),
            KpspItem(3, 53, "Menghitung benda sampai 5?"),
            KpspItem(4, 53, "Menyebut nama kota?"),
            KpspItem(5, 53, "Menirukan 3 angka berurutan?"),
            KpspItem(6, 53, "Membersihkan hidung & kencing mandiri?"),
            KpspItem(7, 53, "Memakai kaus kaki sendiri?"),
            KpspItem(8, 53, "Menggambar orang 3 bagian (kepala-badan-kaki/tangan)?"),
            KpspItem(9, 53, "Mengerti konsep 'setengah'?"),
            KpspItem(10, 53, "Berdongeng singkat dengan isi?"),
        ),
        54 to listOf(
            KpspItem(1, 59, "Melompat 1 kaki di tempat?"),
            KpspItem(2, 59, "Menggambar segitiga meniru?"),
            KpspItem(3, 59, "Menghitung sampai 10?"),
            KpspItem(4, 59, "Menyebut hari dalam seminggu?"),
            KpspItem(5, 59, "Menyetelkan jam (menyebut pagi/siang)?"),
            KpspItem(6, 59, "Membedakan nama hari ini & kemarin?"),
            KpspItem(7, 59, "Membantu pekerjaan rumah (meletakkan piring)?"),
            KpspItem(8, 59, "Menggambar orang 6 bagian?"),
            KpspItem(9, 59, "Mengikat tali sepatu (dibantu)?"),
            KpspItem(10, 59, "Menirukan 4-5 angka berurutan?"),
        ),
        60 to listOf(
            KpspItem(1, 65, "Melompat-lompat 1 kaki berpindah tempat?"),
            KpspItem(2, 65, "Menggambar orang utuh dengan tangan & kaki?"),
            KpspItem(3, 65, "Menyebut nama bulan?"),
            KpspItem(4, 65, "Mengerti kanan-kiri pada diri?"),
            KpspItem(5, 65, "Membaca 3 huruf sederhana?"),
            KpspItem(6, 65, "Menjawab soal cerita sederhana?"),
            KpspItem(7, 65, "Mengikat tali sepatu sendiri?"),
            KpspItem(8, 65, "Menulis nama depan?"),
            KpspItem(9, 65, "Menghitung benda sampai 20?"),
            KpspItem(10, 65, "Berdongeng lengkap dengan urutan kejadian?"),
        ),
        66 to listOf(
            KpspItem(1, 71, "Berlari naik-turun tangga tanpa pegangan?"),
            KpspItem(2, 71, "Menggambar orang lengkap 9 bagian?"),
            KpspItem(3, 71, "Membaca 5-6 huruf?"),
            KpspItem(4, 71, "Menjawab soal cerita dengan alasan?"),
            KpspItem(5, 71, "Mengerti lawan kata berpasangan?"),
            KpspItem(6, 71, "Menulis huruf sederhana?"),
            KpspItem(7, 71, "Berhitung tambah/kurang sederhana?"),
            KpspItem(8, 71, "Mengerti konsep 'pagi, siang, sore, malam'?"),
            KpspItem(9, 71, "Bermain dengan peraturan kelompok?"),
            KpspItem(10, 71, "Menirukan kalimat 8-10 kata?"),
        ),
    )

    private val butirDefault = listOf(
        KpspItem(1, 72, "Apakah anak main/aktivitas sesuai usianya?"),
    )
}
