package com.posyanduku.app.feature.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.StatusFlag
import com.posyanduku.app.data.TargetSummary
import com.posyanduku.app.data.db.ImmunizationEntity
import com.posyanduku.app.data.db.TargetEntity
import com.posyanduku.app.data.seed.PrefsHolder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUi(
    val loading: Boolean = true,
    val keluarga: Int = 0,
    val sasaran: Int = 0,
    val perluTindakan: Int = 0,
    val sum: List<TargetSummary> = emptyList(),
    val imunisasi: List<Pair<TargetEntity, ImmunizationEntity>> = emptyList(),
    val kader: String? = null,
)

class HomeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)

    private val _ui = MutableStateFlow(HomeUi())
    val ui: StateFlow<HomeUi> = _ui

    fun load() {
        viewModelScope.launch {
            val sum = repo.summaries()
            val famCount = repo.families().first().size
            val imunis = repo.immunizations().first()
            val targetsById = repo.targets().first().associateBy { it.id }
            val now = LocalDate.now()
            val perlu = sum.count { it.flag != StatusFlag.OKE }
            val jadwal: List<Pair<TargetEntity, ImmunizationEntity>> = imunis
                .filter { it.diberikan == null }
                .mapNotNull { im: ImmunizationEntity ->
                    val t: TargetEntity? = targetsById[im.targetId]
                    if (t == null) null else (t to im)
                }
                .filter { pair -> !pair.second.jadwal.isAfter(now.plusDays(30)) }
                .sortedBy { pair -> pair.second.jadwal }
            val kader = PrefsHolder.get(getApplication()).kaderName.first()
            _ui.value = HomeUi(false, famCount, sum.size, perlu, sum, jadwal, kader)
        }
    }
}
