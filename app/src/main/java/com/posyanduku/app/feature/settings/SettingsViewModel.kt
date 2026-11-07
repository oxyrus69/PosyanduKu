package com.posyanduku.app.feature.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.seed.PrefsHolder
import com.posyanduku.app.ui.theme.UiScale
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class SettingsUi(
    val scale: UiScale = UiScale.BESAR,
    val kader: String = "",
    val outboxCount: Int = 0,
    val targetCount: Int = 0,
)

class SettingsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val prefs = PrefsHolder.get(app)

    private val _ui = MutableStateFlow(SettingsUi())
    val ui: StateFlow<SettingsUi> = _ui

    init {
        viewModelScope.launch {
            prefs.uiScale.collect { s -> _ui.value = _ui.value.copy(scale = s) }
        }
        viewModelScope.launch {
            prefs.kaderName.collect { k -> _ui.value = _ui.value.copy(kader = k ?: "") }
        }
        viewModelScope.launch {
            repo.targets().collect { t -> _ui.value = _ui.value.copy(targetCount = t.size) }
        }
        viewModelScope.launch {
            repo.outboxCount().collect { c -> _ui.value = _ui.value.copy(outboxCount = c) }
        }
    }

    fun setScale(scale: UiScale) {
        viewModelScope.launch { prefs.setUiScale(scale) }
    }

    fun setKader(name: String) {
        viewModelScope.launch { prefs.setKaderName(name) }
    }
}
