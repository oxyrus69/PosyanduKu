package com.posyanduku.app.data

import android.content.Context
import com.posyanduku.app.data.db.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.UUID

/** Ringkasan sasaran + riwayat ringkas untuk kartu daftar & beranda. */
data class TargetSummary(
    val target: TargetEntity,
    val umurBulan: Int,
    val lastVisit: VisitEntity?,
    val lastVisitDaysAgo: Int,
    val flag: StatusFlag,
    val alasan: String,
)

class Repo(private val db: PosyanduDatabase) {

    fun families() = db.familyDao().observeAll()
    fun targets() = db.targetDao().observeAll()
    fun target(id: String) = db.targetDao().observeById(id)
    fun visits() = db.visitDao().observeAll()
    fun visitsOf(targetId: String) = db.visitDao().observeByTarget(targetId)
    fun immunizations() = db.immunizationDao().observeAll()
    fun immunizationsOf(targetId: String) = db.immunizationDao().observeByTarget(targetId)
    fun outboxCount() = db.outboxDao().observeCount()
    fun outbox() = db.outboxDao().observeAll()

    // ---------- Agregasi baca ----------

    suspend fun summaries(): List<TargetSummary> {
        val targets = db.targetDao().allSync()
        val allVisits = db.visitDao().observeAll().first()
        val byTarget = allVisits.groupBy { it.targetId }
        val today = java.time.LocalDate.now()
        return targets.map { t ->
            val umurBulan = t.tanggalLahir.until(today).toTotalMonths().toInt()
            val lv = byTarget[t.id]?.maxByOrNull { it.tanggal }
            val daysAgo = lv?.tanggal?.until(today)?.days ?: 9999
            val (flag, alasan) = deriveFlag(t, lv, daysAgo)
            TargetSummary(t, umurBulan, lv, daysAgo, flag, alasan)
        }.sortedWith(compareBy({ flagRank(it.flag) }, { -it.lastVisitDaysAgo }))
    }

    private fun flagRank(f: StatusFlag) = when (f) {
        StatusFlag.RUJUK -> 0
        StatusFlag.PERHATIAN -> 1
        StatusFlag.OKE -> 2
    }

    private fun deriveFlag(t: TargetEntity, lv: VisitEntity?, daysAgo: Int): Pair<StatusFlag, String> {
        if (lv != null) {
            if (lv.status == StatusFlag.RUJUK) return StatusFlag.RUJUK to "Rujukan belum ditindak"
            // "perlu tindakan" sebagian besar: belum kunjungan lama, atau status terakhir perhatian
            when (t.type) {
                TargetType.BUMIL -> {
                    if (daysAgo > 28) return StatusFlag.PERHATIAN to "Sudah ${daysAgo} hari tak diperiksa"
                    if (lv.status == StatusFlag.PERHATIAN) return StatusFlag.PERHATIAN to "Temuan terakhir perlu tindak lanjut"
                }
                TargetType.BALITA -> {
                    if (daysAgo > 30) return StatusFlag.PERHATIAN to "Sudah ${daysAgo} hari tak ditimbang"
                    if (lv.status == StatusFlag.PERHATIAN) return StatusFlag.PERHATIAN to "Gizi/tumbuh perlu pantau"
                }
                TargetType.REMAJA_LANSIA -> {
                    if (daysAgo > 90) return StatusFlag.PERHATIAN to "Sudah ${daysAgo} hari tak kontrol"
                    if (lv.status == StatusFlag.PERHATIAN) return StatusFlag.PERHATIAN to "Tekanan/IMT perlu dipantau"
                }
            }
            return StatusFlag.OKE to "Kondisi baik"
        }
        // belum pernah diperiksa sama sekali
        return StatusFlag.PERHATIAN to "Belum pernah dicatat"
    }

    // ---------- Tulis (dengan outbox idempoten) ----------

    suspend fun saveTarget(t: TargetEntity) {
        db.targetDao().upsert(t)
        outbox("target", t.id)
    }

    suspend fun saveFamily(f: FamilyEntity) {
        db.familyDao().upsert(f)
    }

    suspend fun saveVisit(v: VisitEntity) {
        db.visitDao().upsert(v)
        outbox("visit", v.id)
    }

    suspend fun saveImmunization(i: ImmunizationEntity) {
        db.immunizationDao().upsert(i)
        outbox("imunisasi", i.id)
    }

    suspend fun newId(prefix: String) = "${'$'}prefix-${'$'}{UUID.randomUUID()}"

    private suspend fun outbox(kind: String, refId: String) {
        db.outboxDao().insert(OutboxEntity(UUID.randomUUID().toString(), kind, refId, System.currentTimeMillis()))
    }

    companion object {
        @Volatile private var repo: Repo? = null
        fun get(context: Context): Repo =
            repo ?: synchronized(this) { repo ?: Repo(PosyanduDatabase.get(context)).also { repo = it } }
    }
}
