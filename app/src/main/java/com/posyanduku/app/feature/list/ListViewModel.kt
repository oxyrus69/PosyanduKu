package com.posyanduku.app.feature.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.posyanduku.app.data.Repo
import com.posyanduku.app.data.TargetSummary
import com.posyanduku.app.data.TargetType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ListUi(
    val loading: Boolean = true,
    val all: List<TargetSummary> = emptyList(),
    val filter: TargetType? = null,
    val query: String = "",
) {
    val shown: List<TargetSummary> get() = all.filter { s ->
        (filter == null || s.target.type == filter) &&
        (query.isBlank() || s.target.nama.contains(query, true) || s.target.type.label.contains(query, true))
    }.sortedBy { it.target.nama }
}

class ListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repo.get(app)
    private val _ui = MutableStateFlow(ListUi())
    val ui: StateFlow<ListUi> = _ui

    init { reload() }

    fun reload() {
        viewModelScope.launch {
            _ui.update { it.copy(loading = true) }
            val sum = repo.summaries()
            _ui.update { it.copy(loading = false, all = sum) }
        }
    }

    fun setFilter(t: TargetType?) = _ui.update { it.copy(filter = t) }
    fun setQuery(q: String) = _ui.update { it.copy(query = q) }
}
