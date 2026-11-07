package com.posyanduku.app.feature.visit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.anthro.ZScore
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.data.db.TargetEntity
import com.posyanduku.app.data.db.VisitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class VisitUi(
    val target: TargetEntity? = null,
    val bb: String = "", val tb: String = "",
    val lila: String = "", val imt: String = "",
    val tensiSys: String = "", val tensiDia: String = "",
    val catatan: String = "",
    val manualFlag: StatusFlag? = null,
    val saving: Boolean = false, val saved: Boolean = false,
) {
    val umurBulan: Int get() = target?.tanggalLahir?.until(LocalDate.now())?.toTotalMonths()?.toInt() ?: 0

    /** Z-score langsung dari input (untuk balita). */
    data class Live(val zBbu: Double?, val zTbu: Double?, val zBbtb: Double?, val status: ZScore.StatusGizi)
    val live: Live get() {
        val t = target ?: return Live(null, null, null, ZScore.StatusGizi.OKE)
        if (t.type != TargetType.BALITA) return Live(null, null, null, ZScore.StatusGizi.OKE)
        val bb = bb.toDoubleOrNull(); val tb = tb.toDoubleOrNull()
        val umur = umurBulan.toDouble()
        val z1 = if (bb != null) ZScore.bbu(t.sex, umur, bb)?.z else null
        val z2 = if (tb != null) ZScore.tbu(t.sex, umur, tb)?.z else null
        val z3 = if (bb != null && tb != null) ZScore.bbtb(t.sex, tb, bb)?.z else null
        val status = ZScore.statusGizi(z1, z2, z3)
        return Live(z1, z2, z3, status)
    }

    val imtHitung: Double? get() {
        val bb = bb.toDoubleOrNull() ?: return null
        val tb = tb.toDoubleOrNull() ?: return null
        if (tb < 50.0) return null
        val m = tb / 100
        return bb / (m * m)
    }
}

class VisitViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val _ui = MutableStateFlow(VisitUi())
    val ui: StateFlow<VisitUi> = _ui

    fun load(id: String) {
        viewModelScope.launch {
            repo.target(id).collect { t -> _ui.update { it.copy(target = t) } }
        }
    }

    fun upd(mut: VisitUi.() -> VisitUi) = _ui.update { it.mut() }
    fun setBb(v: String) = upd { copy(bb = v) }
    fun setTb(v: String) = upd { copy(tb = v) }
    fun setLila(v: String) = upd { copy(lila = v) }
    fun setTensiSys(v: String) = upd { copy(tensiSys = v) }
    fun setTensiDia(v: String) = upd { copy(tensiDia = v) }
    fun setCatatan(v: String) = upd { copy(catatan = v) }
    fun setFlag(f: StatusFlag?) = upd { copy(manualFlag = f) }

    fun simpan(onDone: () -> Unit) {
        val s = _ui.value; val t = s.target ?: return
        _ui.update { it.copy(saving = true) }
        viewModelScope.launch {
            val live = s.live
            val status: StatusFlag = s.manualFlag ?: when (t.type) {
                TargetType.BALITA -> when (live.status) {
                    ZScore.StatusGizi.RUJUK -> StatusFlag.RUJUK
                    ZScore.StatusGizi.PERHATIAN -> StatusFlag.PERHATIAN
                    ZScore.StatusGizi.OKE -> StatusFlag.OKE
                }
                TargetType.BUMIL -> {
                    val lila = s.lila.toDoubleOrNull()
                    when {
                        lila != null && lila < 23.5 -> StatusFlag.RUJUK
                        else -> StatusFlag.OKE
                    }
                }
                TargetType.REMAJA_LANSIA -> {
                    val imt = s.imtHitung
                    val sys = s.tensiSys.toIntOrNull()
                    when {
                        sys != null && sys >= 180 -> StatusFlag.RUJUK
                        sys != null && sys >= 140 -> StatusFlag.PERHATIAN
                        imt != null && imt >= 30 -> StatusFlag.PERHATIAN
                        else -> StatusFlag.OKE
                    }
                }
            }
            val note = buildString {
                if (s.catatan.isNotBlank()) append(s.catatan.trim())
                if (t.type == TargetType.REMAJA_LANSIA && s.tensiSys.isNotBlank()) {
                    if (isNotEmpty()) append(" • ")
                    append("Tensi ${s.tensiSys}/${s.tensiDia.ifBlank { "-" }}")
                }
            }.ifBlank { "Kunjungan Posyandu rutin." }

            val v = VisitEntity(
                id = "v-${UUID.randomUUID()}",
                targetId = t.id,
                tanggal = LocalDate.now(),
                jenis = "Posyandu",
                bb = s.bb.toDoubleOrNull(),
                tb = s.tb.toDoubleOrNull(),
                imt = if (t.type == TargetType.REMAJA_LANSIA) s.imtHitung else null,
                lila = if (t.type == TargetType.BUMIL) s.lila.toDoubleOrNull() else null,
                status = status,
                catatan = note,
                zBbu = live.zBbu, zTbu = live.zTbu, zBbtb = live.zBbtb,
                sdidtkSkor = null, sdidtkStatus = null,
            )
            repo.saveVisit(v)
            _ui.update { it.copy(saving = false, saved = true) }
            onDone()
        }
    }
}
