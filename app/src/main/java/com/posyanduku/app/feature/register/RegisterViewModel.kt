package com.posyanduku.app.feature.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.Sex
import com.posyanduku.app.data.TargetType
import com.posyanduku.app.data.db.FamilyEntity
import com.posyanduku.app.data.db.TargetEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

data class RegisterUi(
    val families: List<FamilyEntity> = emptyList(),
    val type: TargetType = TargetType.BALITA,
    val useNewFamily: Boolean = false,
    val familyId: String? = null,
    val newNoKk: String = "", val newKepala: String = "",
    val newRt: String = "", val newRw: String = "",
    val nama: String = "",
    val sex: Sex = Sex.P,
    val tahun: String = "", val bulan: String = "", val tanggal: String = "",
    val umurTahun: String = "",              // alternatif usia
    val umurBulan: String = "",
    val hphtTahun: String = "", val hphtBulan: String = "", val hphtTanggal: String = "",
    val hamilKe: String = "",
    val namaIbu: String = "",
    val saving: Boolean = false, val saved: Boolean = false,
) {
    val tanggalLahir: LocalDate? get() {
        val ty = tahun.toIntOrNull(); val tb = bulan.toIntOrNull(); val tt = tanggal.toIntOrNull()
        if (ty != null && tb != null && tt != null && ty in 1900..2100 && tb in 1..12 && tt in 1..31) {
            return runCatching { LocalDate.of(ty, tb, tt) }.getOrNull()
        }
        // gagal ke usia: perkirakan tanggal lahir mundur usia
        val totalBulan = (umurTahun.toIntOrNull() ?: 0) * 12 + (umurBulan.toIntOrNull() ?: 0)
        if (totalBulan > 0) {
            return LocalDate.now().minusMonths(totalBulan.toLong())
        }
        return null
    }

    val hpht: LocalDate? get() {
        val ty = hphtTahun.toIntOrNull(); val tb = hphtBulan.toIntOrNull(); val tt = hphtTanggal.toIntOrNull()
        if (ty != null && tb != null && tt != null) return runCatching { LocalDate.of(ty, tb, tt) }.getOrNull()
        return null
    }

    val valid: Boolean get() = nama.isNotBlank() && tanggalLahir != null &&
            (type != TargetType.BUMIL || hpht != null) &&
            (!useNewFamily || (newKepala.isNotBlank() && newNoKk.isNotBlank())) &&
            (useNewFamily || familyId != null)
}

class RegisterViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val _ui = MutableStateFlow(RegisterUi())
    val ui: StateFlow<RegisterUi> = _ui

    init {
        viewModelScope.launch {
            repo.families().collect { f -> _ui.update { it.copy(families = f) } }
        }
    }

    fun set(mut: RegisterUi.() -> RegisterUi) = _ui.update { it.mut() }
    fun setType(t: TargetType) = set { copy(type = t) }
    fun setNama(v: String) = set { copy(nama = v) }
    fun setSex(v: Sex) = set { copy(sex = v) }
    fun setUseNew(v: Boolean) = set { copy(useNewFamily = v) }
    fun setFamily(v: String) = set { copy(familyId = v) }
    fun setNewNoKk(v: String) = set { copy(newNoKk = v) }
    fun setNewKepala(v: String) = set { copy(newKepala = v) }
    fun setNewRt(v: String) = set { copy(newRt = v) }
    fun setNewRw(v: String) = set { copy(newRw = v) }
    fun setTahun(v: String) = set { copy(tahun = v) }
    fun setBulan(v: String) = set { copy(bulan = v) }
    fun setTanggal(v: String) = set { copy(tanggal = v) }
    fun setUmurTahun(v: String) = set { copy(umurTahun = v) }
    fun setUmurBulan(v: String) = set { copy(umurBulan = v) }
    fun setHphtTahun(v: String) = set { copy(hphtTahun = v) }
    fun setHphtBulan(v: String) = set { copy(hphtBulan = v) }
    fun setHphtTanggal(v: String) = set { copy(hphtTanggal = v) }
    fun setHamilKe(v: String) = set { copy(hamilKe = v) }
    fun setNamaIbu(v: String) = set { copy(namaIbu = v) }

    fun simpan(onDone: () -> Unit) {
        val s = _ui.value
        if (!s.valid) return
        _ui.update { it.copy(saving = true) }
        viewModelScope.launch {
            var famId = s.familyId
            if (s.useNewFamily) {
                famId = "fam-${UUID.randomUUID()}"
                repo.saveFamily(FamilyEntity(famId, s.newNoKk, s.newKepala, s.newRt, s.newRw, 1))
            }
            val t = TargetEntity(
                id = "t-${UUID.randomUUID()}",
                familyId = famId,
                nama = s.nama.trim(),
                tanggalLahir = s.tanggalLahir!!,
                sex = s.sex,
                type = s.type,
                nik = null,
                namaIbu = s.namaIbu.ifBlank { null },
                hamilKe = s.hamilKe.toIntOrNull(),
                hpht = s.hpht,
                upSwipeDate = null,
            )
            repo.saveTarget(t)
            _ui.update { it.copy(saving = false, saved = true) }
            onDone()
        }
    }
}
