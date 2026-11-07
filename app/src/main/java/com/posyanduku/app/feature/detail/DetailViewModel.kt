package com.posyanduku.app.feature.detail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.db.ImmunizationEntity
import com.posyanduku.app.data.db.TargetEntity
import com.posyanduku.app.data.db.VisitEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class DetailUi(
    val target: TargetEntity? = null,
    val visits: List<VisitEntity> = emptyList(),
    val immunizations: List<ImmunizationEntity> = emptyList(),
)

class DetailViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val _ui = MutableStateFlow(DetailUi())
    val ui: StateFlow<DetailUi> = _ui

    fun load(id: String) {
        viewModelScope.launch {
            repo.target(id).collect { t ->
                _ui.value = _ui.value.copy(target = t)
            }
        }
        viewModelScope.launch {
            repo.visitsOf(id).collect { v ->
                _ui.value = _ui.value.copy(visits = v)
            }
        }
        viewModelScope.launch {
            repo.immunizationsOf(id).collect { im ->
                _ui.value = _ui.value.copy(immunizations = im.sortedBy { it.jadwal })
            }
        }
    }

    fun markGiven(im: ImmunizationEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            repo.saveImmunization(im.copy(diberikan = java.time.LocalDate.now()))
            onDone()
        }
    }
}
