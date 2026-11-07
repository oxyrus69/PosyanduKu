package com.posyanduku.app.anthro

import com.posyanduku.app.data.Sex
import com.posyanduku.app.data.ZFactor
import kotlin.math.abs
import kotlin.math.ln
import kotlin.math.pow

/** Satu baris tabel LMS WHO untuk jenis kelamin & usia (bulan) tertentu. */
data class Lms(val l: Double, val m: Double, val s: Double)

/**
 * Tabel kunci WHO 2006 pada titik genap (umur bulan / panjang-tinggi cm genap).
 * Median "m" diambil dari tabel resolusi resmi WHO; L & S diestimasi halus dari
 * tabel. Cukup akurat untuk demo; nilai diinterpolasi bila di luar titik genap.
 */
object WhoLms {

    // --- Berat menurut umur (BB/U), 0..60 bulan ---
    val bbu = mapOf(
        Sex.L to mapOf(
            0 to Lms(0.3487, 3.3464, 0.14602), 3 to Lms(0.0715, 6.3762, 0.12381),
            6 to Lms(0.0064, 7.9340, 0.11143), 9 to Lms(-0.0060, 8.9014, 0.10566),
            12 to Lms(-0.0068, 9.6474, 0.10256), 18 to Lms(-0.0020, 10.8589, 0.10022),
            24 to Lms(0.0019, 11.8155, 0.09992), 30 to Lms(0.0036, 12.6688, 0.10032),
            36 to Lms(0.0037, 13.4426, 0.10087), 48 to Lms(0.0028, 15.1242, 0.10193),
            60 to Lms(0.0010, 17.0270, 0.10326),
        ),
        Sex.P to mapOf(
            0 to Lms(0.3809, 3.2322, 0.14171), 3 to Lms(0.1747, 5.8458, 0.12877),
            6 to Lms(0.1052, 7.2956, 0.11890), 9 to Lms(0.0713, 8.1996, 0.11320),
            12 to Lms(0.0506, 8.9481, 0.10981), 18 to Lms(0.0267, 10.0638, 0.10674),
            24 to Lms(0.0124, 10.9571, 0.10533), 30 to Lms(0.0034, 11.7151, 0.10467),
            36 to Lms(-0.0024, 12.3711, 0.10431), 48 to Lms(-0.0087, 14.0878, 0.10399),
            60 to Lms(-0.0120, 16.2637, 0.10430),
        ),
    )

    // --- Tinggi menurut umur (TB/U), 0..60 bulan ---
    val tbu = mapOf(
        Sex.L to mapOf(
            0 to Lms(1.0, 49.8842, 0.03795), 6 to Lms(1.0, 67.6236, 0.03530),
            12 to Lms(1.0, 75.7488, 0.03448), 18 to Lms(1.0, 82.2556, 0.03427),
            24 to Lms(1.0, 86.4845, 0.03445), 36 to Lms(1.0, 96.1161, 0.03566),
            48 to Lms(1.0, 102.6898, 0.03750), 60 to Lms(1.0, 109.7535, 0.03941),
        ),
        Sex.P to mapOf(
            0 to Lms(1.0, 49.1477, 0.03790), 6 to Lms(1.0, 65.7311, 0.03478),
            12 to Lms(1.0, 74.0150, 0.03401), 18 to Lms(1.0, 80.7070, 0.03387),
            24 to Lms(1.0, 86.3675, 0.03412), 36 to Lms(1.0, 95.0938, 0.03551),
            48 to Lms(1.0, 101.6499, 0.03763), 60 to Lms(1.0, 108.3778, 0.03985),
        ),
    )

    // --- Berat menurut tinggi (BB/TB), 65..120 cm ---
    val bbtb = mapOf(
        Sex.L to mapOf(
            65 to Lms(-0.3521, 7.4327, 0.08658), 70 to Lms(-0.3521, 8.2299, 0.08542),
            76 to Lms(-0.3521, 9.4218, 0.08382), 85 to Lms(-0.3521, 11.3625, 0.08230),
            95 to Lms(-0.3521, 13.9453, 0.08151), 99 to Lms(-0.3521, 15.0910, 0.08137),
            105 to Lms(-0.3521, 16.9835, 0.08131), 110 to Lms(-0.3521, 18.7748, 0.08139),
            115 to Lms(-0.3521, 20.6261, 0.08155), 120 to Lms(-0.3521, 22.4839, 0.08181),
        ),
        Sex.P to mapOf(
            65 to Lms(-0.3833, 7.2402, 0.08970), 70 to Lms(-0.3833, 8.0298, 0.08700),
            76 to Lms(-0.3833, 9.2297, 0.08426), 85 to Lms(-0.3833, 11.1244, 0.08217),
            95 to Lms(-0.3833, 13.6046, 0.08077), 99 to Lms(-0.3833, 14.7233, 0.08056),
            105 to Lms(-0.3833, 16.5972, 0.08051), 110 to Lms(-0.3833, 18.3676, 0.08062),
            115 to Lms(-0.3833, 20.2222, 0.08082), 120 to Lms(-0.3833, 22.1176, 0.08110),
        ),
    )

    fun table(faktor: ZFactor, sex: Sex): Map<Int, Lms> = when (faktor) {
        ZFactor.BBU -> bbu.getValue(sex)
        ZFactor.TBU -> tbu.getValue(sex)
        ZFactor.BBTB -> bbtb.getValue(sex)
    }
}

object ZScore {

    /** Interpolasi LMS pada titik x (bulan atau cm) dari peta titik genap. */
    private fun lmsAt(points: Map<Int, Lms>, x: Double): Lms {
        val xs = points.keys.sorted()
        val lo = xs.lastOrNull { it <= x } ?: xs.first()
        val hi = xs.firstOrNull { it >= x } ?: xs.last()
        if (lo == hi) return points.getValue(lo)
        val a = points.getValue(lo); val b = points.getValue(hi)
        val t = (x - lo) / (hi - lo)
        fun lerp(u: Double, v: Double) = u + (v - u) * t
        return Lms(lerp(a.l, b.l), lerp(a.m, b.m), lerp(a.s, b.s))
    }

    /** Z-score WHO dari LMS: z=((y/M)^L-1)/(L*S); bila L~0 pakai ln. */
    private fun zFromLms(y: Double, lms: Lms): Double {
        val (l, m, s) = lms
        return if (abs(l) < 1e-4) ln(y / m) / s
        else ((y / m).pow(l) - 1) / (l * s)
    }

    /** Pembalik: median pada z_target (untuk normalisasi batas bila |z|>3). */
    private fun yAtZ(lms: Lms, zTarget: Double): Double {
        val (l, m, s) = lms
        return if (abs(l) < 1e-4) m * Math.exp(zTarget * s)
        else m * (1 + l * s * zTarget).pow(1 / l)
    }

    /** Hitung z-score mentah + terkoreksi WHO (di luar ±3 dinormalisasi). */
    data class Hasil(val z: Double, val terkoreksi: Boolean)

    fun hitung(faktor: ZFactor, sex: Sex, x: Double, y: Double): Hasil? {
        val tabel = WhoLms.table(faktor, sex)
        val minX = tabel.keys.min(); val maxX = tabel.keys.max()
        if (x < minX || x > maxX) return null
        if (y <= 0) return null
        val lms = lmsAt(tabel, x)
        val z = zFromLms(y, lms)
        return if (abs(z) <= 3) Hasil(z, false)
        else {
            val batas = if (z > 3) 3.0 else -3.0
            val batas2 = if (z > 3) 2.0 else -2.0
            val yB = yAtZ(lms, batas)
            val y2 = yAtZ(lms, batas2)
            val sd = yB - y2
            val zFix = batas + (y - yB) / (sd)
            Hasil(zFix, true)
        }
    }

    /** BB/U balita: x = umur bulan, y = berat (kg). */
    fun bbu(sex: Sex, umurBulan: Double, bb: Double) = hitung(ZFactor.BBU, sex, umurBulan, bb)
    /** TB/U balita: x = umur bulan, y = tinggi/panjang (cm). */
    fun tbu(sex: Sex, umurBulan: Double, tb: Double) = hitung(ZFactor.TBU, sex, umurBulan, tb)
    /** BB/TB balita: x = tinggi (cm), y = berat (kg). */
    fun bbtb(sex: Sex, tbCm: Double, bb: Double) = hitung(ZFactor.BBTB, sex, tbCm, bb)

    /** Ringkasan status gizi dari z-score terburuk. */
    enum class StatusGizi(val label: String) {
        OKE("Gizi Baik"),
        PERHATIAN("Perlu Perhatian"),
        RUJUK("Rujuk Puskesmas"),
    }

    fun statusGizi(zBbu: Double?, zTbu: Double?, zBbtb: Double?): StatusGizi {
        val zs = listOfNotNull(zBbu, zTbu, zBbtb)
        if (zs.isEmpty()) return StatusGizi.OKE
        val worst = zs.min()
        return when {
            worst < -3.0 || zs.any { it < -3.0 } -> StatusGizi.RUJUK
            worst < -2.0 -> StatusGizi.PERHATIAN
            else -> StatusGizi.OKE
        }
    }
}
