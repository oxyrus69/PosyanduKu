package com.posyanduku.app.feature.sdidtk

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Kpsp
import com.posyanduku.app.data.KpspItem
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.db.TargetEntity
import com.posyanduku.app.data.db.VisitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class SdidtkUi(
    val target: TargetEntity? = null,
    val umurBulan: Int = 0,
    val items: List<KpspItem> = emptyList(),
    val answers: Map<Int, Boolean> = emptyMap(),
    val saving: Boolean = false, val saved: Boolean = false,
) {
    val skor: Int get() = answers.values.count { it }
    val hasil: Kpsp.Hasil get() = Kpsp.skorKeHasil(skor)
    val allAnswered: Boolean get() = items.isNotEmpty() && answers.size == items.size
    val saran: String get() = when (hasil) {
        Kpsp.Hasil.SESUAI -> "Pertahankan stimulasi: ajak bicara, bermain, dan dorong aktivitas sesuai usia."
        Kpsp.Hasil.MERAGUKAN -> "Latih 2 kali/hari pada butir yang belum bisa, lalu ulangi skrining 2 minggu. Bila tetap meragukan → rujuk."
        Kpsp.Hasil.PENYIMPANGAN -> "Rujuk ke Puskesmas/bidan segera untuk intervensi dini. Tetap beri stimulasi harian."
    }
}

class SdidtkViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val _ui = MutableStateFlow(SdidtkUi())
    val ui: StateFlow<SdidtkUi> = _ui

    fun load(id: String) {
        viewModelScope.launch {
            repo.target(id).collect { t ->
                if (t != null) {
                    val umur = t.tanggalLahir.until(LocalDate.now()).toTotalMonths().toInt()
                    _ui.update { it.copy(target = t, umurBulan = umur, items = Kpsp.butirUntuk(umur)) }
                }
            }
        }
    }

    fun answer(nomor: Int, ya: Boolean) = _ui.update {
        it.copy(answers = it.answers + (nomor to ya))
    }

    fun simpan(onDone: () -> Unit) {
        val s = _ui.value; val t = s.target ?: return
        _ui.update { it.copy(saving = true) }
        viewModelScope.launch {
            val status = when (s.hasil) {
                Kpsp.Hasil.SESUAI -> StatusFlag.OKE
                Kpsp.Hasil.MERAGUKAN -> StatusFlag.PERHATIAN
                Kpsp.Hasil.PENYIMPANGAN -> StatusFlag.RUJUK
            }
            val detail = s.items.joinToString("\n") { item ->
                "${item.nomor}. ${item.teks} → ${if (s.answers[item.nomor] == true) "Ya" else "Tidak"}"
            }
            val v = VisitEntity(
                id = "sd-${UUID.randomUUID()}",
                targetId = t.id,
                tanggal = LocalDate.now(),
                jenis = "SDIDTK",
                bb = null, tb = null, imt = null, lila = null,
                status = status,
                catatan = "Skrining KPSP (SDIDTK): skor ${s.skor} dari ${s.items.size} — ${s.hasil.label}.",
                zBbu = null, zTbu = null, zBbtb = null,
                sdidtkSkor = s.skor, sdidtkStatus = status,
            )
            repo.saveVisit(v)
            _ui.update { it.copy(saving = false, saved = true) }
            onDone()
        }
    }
}
