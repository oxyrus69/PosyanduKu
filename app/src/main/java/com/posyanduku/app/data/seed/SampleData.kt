package com.posyanduku.app.data.seed

import com.posyanduku.app.data.Sex
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.data.db.*

object SampleData {

    private fun day(offsetDays: Long) = java.time.LocalDate.now().minusDays(offsetDays)
    /** Umur orang dewasa dalam tahun; day(n*365) salah karena setahun ≈ 365,25 hari. */
    private fun years(n: Long) = java.time.LocalDate.now().minusYears(n)
    /** Umur balita dalam bulan, dibulatkan ke hari. */
    private fun months(n: Long) = java.time.LocalDate.now().minusMonths(n)

    fun families() = listOf(
        FamilyEntity("fam1", "3204.0512.0001", "Surya Wijaya", "03", "07", 4),
        FamilyEntity("fam2", "3204.0512.0002", "Dedi Kurniawan", "03", "07", 5),
        FamilyEntity("fam3", "3204.0512.0003", "Agus Salim", "04", "07", 3),
        FamilyEntity("fam4", "3204.0512.0004", "Joko Prasetyo", "04", "08", 4),
        FamilyEntity("fam5", "3204.0512.0005", "Umar Hidayat", "05", "08", 6),
        FamilyEntity("fam6", "3204.0512.0006", "Budi Santoso", "05", "08", 4),
        FamilyEntity("fam7", "3204.0512.0007", "Eko Purnomo", "06", "09", 3),
        FamilyEntity("fam8", "3204.0512.0008", "Rudi Hartono", "06", "09", 5),
        FamilyEntity("fam9", "3204.0512.0009", "Slamet Riyadi", "07", "09", 4),
        FamilyEntity("fam10", "3204.0512.0010", "Herman Susilo", "07", "09", 4),
    )

    fun targets() = listOf(
        // ---- Ibu hamil ----
        TargetEntity("t_bumil1", "fam1", "Siti Rahayu", years(29), Sex.P, TargetType.BUMIL,
            "3204055205960001", null, 2, day(14 * 7), day(2)),
        TargetEntity("t_bumil2", "fam2", "Ayu Lestari", years(32), Sex.P, TargetType.BUMIL,
            "3204055205900002", null, 1, day(26 * 7), day(30)), // telat >3 minggu
        TargetEntity("t_bumil3", "fam3", "Dwi Puspita", years(26), Sex.P, TargetType.BUMIL,
            "3204055205990003", null, 3, day(36 * 7), day(7)),
        TargetEntity("t_bumil4", "fam4", "Ratna Sari", years(33), Sex.P, TargetType.BUMIL,
            "3204055205920004", null, 2, day(20 * 7), day(0)),

        // ---- Bayi & Balita ----
        TargetEntity("t_balita1", "fam1", "Alya Putri", months(60), Sex.P, TargetType.BALITA,
            "3204055501000011", "Siti Rahayu", null, null, day(35)), // LAMA tak ditimbang
        TargetEntity("t_balita2", "fam2", "Bima Pratama", months(14), Sex.L, TargetType.BALITA,
            "3204055502000012", "Ayu Lestari", null, null, day(5)),
        TargetEntity("t_balita3", "fam3", "Citra Ayu", months(30), Sex.P, TargetType.BALITA,
            "3204055503000013", "Dwi Puspita", null, null, day(62)), // sudah 2 bulan tak ditimbang
        TargetEntity("t_balita4", "fam5", "Danu Firmansyah", months(6), Sex.L, TargetType.BALITA,
            "3204055504000014", "Nina Marlina", null, null, day(10)),
        TargetEntity("t_balita5", "fam6", "Eko Wibowo", months(20), Sex.L, TargetType.BALITA,
            "3204055505000015", "Yuni Astuti", null, null, day(1)),
        TargetEntity("t_balita6", "fam7", "Fajar Ramadhan", months(38), Sex.L, TargetType.BALITA,
            "3204055506000016", "Dewi Anggraini", null, null, day(21)),
        TargetEntity("t_balita7", "fam8", "Gita Savitri", months(10), Sex.P, TargetType.BALITA,
            "3204055507000017", "Rina Kurnia", null, null, day(0)),
        TargetEntity("t_balita8", "fam9", "Hendra Gunawan", months(48), Sex.L, TargetType.BALITA,
            "3204055508000018", "Lina Wati", null, null, day(49)),
        TargetEntity("t_balita9", "fam10", "Intan Permata", months(16), Sex.P, TargetType.BALITA,
            "3204055509000019", "Sari Melati", null, null, day(14)),
        TargetEntity("t_balita10", "fam4", "Joko Ananda", months(30), Sex.L, TargetType.BALITA,
            "3204055510000020", "Ratna Sari", null, null, day(8)),

        // ---- Remaja & Lansia ----
        TargetEntity("t_remlan1", "fam5", "Nina Marlina", years(35), Sex.P, TargetType.REMAJA_LANSIA,
            "3204055211000021", null, null, null, day(44)),
        TargetEntity("t_remlan2", "fam6", "Yuni Astuti", years(48), Sex.P, TargetType.REMAJA_LANSIA,
            "3204055212000022", null, null, null, day(3)),
        TargetEntity("t_remlan3", "fam7", "Dewi Anggraini", years(62), Sex.P, TargetType.REMAJA_LANSIA,
            "3204055213000023", null, null, null, day(75)),
        TargetEntity("t_remlan4", "fam8", "Rina Kurnia", years(18), Sex.P, TargetType.REMAJA_LANSIA,
            "3204055214000024", null, null, null, day(9)),
        TargetEntity("t_remlan5", "fam9", "Lina Wati", years(55), Sex.L, TargetType.REMAJA_LANSIA,
            "3204055215000025", null, null, null, day(0)),
        TargetEntity("t_remlan6", "fam10", "Sari Melati", years(29), Sex.P, TargetType.REMAJA_LANSIA,
            "3204055216000026", null, null, null, day(18)),
    )

    /** Pasangan BB/TB yang disetel per sasaran supaya dalam indikator tampil realistis. */
    fun visits(): List<VisitEntity> {
        val v = ArrayList<VisitEntity>()
        fun add(id: String, t: String, umurBulanDiInput: Int, bb: Double?, tb: Double?,
                status: StatusFlag, note: String, zB: Double? = null, zT: Double? = null, zBb: Double? = null,
                sdidtkSkor: Int? = null, sdidtkStatus: StatusFlag? = null, hariLalu: Long) {
            v.add(VisitEntity(id, t, java.time.LocalDate.now().minusDays(hariLalu),
                "Posyandu", bb, tb, null, null, status, note, zB, zT, zBb, sdidtkSkor, sdidtkStatus))
        }
        // Balita2 Bima (14 bl L): gizi baik
        add("v1", "t_balita2", 14, 9.6, 76.0, StatusFlag.OKE, "Tumbuh sesuai jalur.", -0.4, 0.4, 0.6, 9, StatusFlag.OKE, 5)
        // Balita3 Citra (30 bl P): perlu perhatian (BB/U < -2)
        add("v2", "t_balita3", 30, 9.3, 82.0, StatusFlag.PERHATIAN, "Berat kurang dari jalur. Naikkan porsi & pantau 2 minggu.", -2.35, -1.2, -1.8, 8, StatusFlag.PERHATIAN, 62)
        // Balita4 Danu (6 bl L): perhatian ringan
        add("v3", "t_balita4", 6, 7.0, 65.0, StatusFlag.PERHATIAN, "Berat sedikit lambat. ASI + MP-ASI padat gizi.", -1.9, -0.5, -0.5, null, null, 10)
        // Balita6 Fajar (38 bl L): RUJUK (z < -3)
        add("v4", "t_balita6", 38, 10.4, 92.0, StatusFlag.RUJUK, "Berat sangat kurang. Rujuk ke Puskesmas hari ini.", -3.35, -1.6, -2.7, 6, StatusFlag.RUJUK, 21)
        // Balita7 Gita (10 bl P): oke
        add("v5", "t_balita7", 10, 8.3, 72.0, StatusFlag.OKE, "Tumbuh baik.", -0.3, 0.4, 0.5, null, null, 0)
        // Balita9 Intan (16 bl P): oke
        add("v6", "t_balita9", 16, 10.0, 78.0, StatusFlag.OKE, "Tumbuh sesuai usia.", -0.4, 0.4, 0.6, 8, StatusFlag.OKE, 14)
        // Bumil1 Siti (14 mgg): ANC trimester awal oke
        v.add(VisitEntity("v7", "t_bumil1", java.time.LocalDate.now().minusDays(2), "Posyandu",
            52.0, null, null, 24.0, StatusFlag.OKE, "Hamil 14 minggu, tekanan darah normal.", null, null, null, null, null))
        // Bumil2 Ayu (26 mgg): perhatian LILA <23.5
        v.add(VisitEntity("v8", "t_bumil2", java.time.LocalDate.now().minusDays(30), "Posyandu",
            55.0, null, null, 22.0, StatusFlag.PERHATIAN, "LiLA 22 cm di bawah batas. KIE gizi & pantau.", null, null, null, null, null))
        // Remlan3 Dewi (62 th): IMT tinggi → perhatian
        v.add(VisitEntity("v9", "t_remlan3", java.time.LocalDate.now().minusDays(75), "Posyandu",
            82.0, 155.0, 34.1, null, StatusFlag.PERHATIAN, "IMT 34 (obesitas). Edukasi jalan kaki & gula.", null, null, null, null, null))
        // Remlan5 Lina (55 th): tensi perlu diulang
        v.add(VisitEntity("v10", "t_remlan5", java.time.LocalDate.now(), "Posyandu",
            68.0, 160.0, 26.5, null, StatusFlag.OKE, "IMT lebih berat ringan; ulangi cek tekanan darah.", null, null, null, null, null))
        return v
    }

    fun immunizations(): List<ImmunizationEntity> {
        val now = java.time.LocalDate.now()
        val out = ArrayList<ImmunizationEntity>()
        fun add(id: String, t: String, vaksin: String, dosis: String, jadwalOffset: Long, diberikanOffset: Long? = null) {
            out.add(ImmunizationEntity(id, t, vaksin, dosis, now.plusDays(jadwalOffset),
                diberikanOffset?.let { now.plusDays(it) }))
        }
        // Danu (6 bl): jadwal dekat
        add("i1", "t_balita4", "Campak/MR", "Dosis 1", 12)         // jatuh tempo 12 hari lagi
        add("i2", "t_balita4", "DPT-HB-Hib", "Dosis 3", -14, -14)  // sudah
        // Bima (14 bl): perlu booster
        add("i3", "t_balita2", "DPT-HB-Hib", "Booster", 5)         // 5 hari lagi
        add("i4", "t_balita2", "Campak/MR", "Dosis 1", -60, -60)
        // Fajar (38 bl): baru jadwal
        add("i5", "t_balita6", "Campak/MR", "Dosis 2", 25)
        // Gita (10 bl): baru lalu
        add("i6", "t_balita7", "DPT-HB-Hib", "Dosis 3", -30, -30)
        // Intan (16 bl): selesai
        add("i7", "t_balita9", "Campak/MR", "Dosis 1", -120, -120)
        add("i8", "t_balita9", "DPT-HB-Hib", "Booster", -5)        // lewat 5 hari, belum diberi
        // Citra (30 bl): lewat
        add("i9", "t_balita3", "Campak/MR", "Dosis 2", -20)        // lewat 20 hari
        return out
    }
}
