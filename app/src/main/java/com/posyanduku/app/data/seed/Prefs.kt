package com.posyanduku.app.data.seed

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.posyanduku.app.PosyanduApp
import com.posyanduku.app.data.db.PosyanduDatabase
import com.posyanduku.app.ui.theme.UiScale
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val KEY_SEEDED = stringPreferencesKey("seeded_v1")
private val KEY_UI_SCALE = stringPreferencesKey("ui_scale")
private val KEY_KADER = stringPreferencesKey("kader_name")

class Prefs(private val context: Context) {

    private val ds get() = PosyanduApp.dataStoreOf(context)

    val uiScale: Flow<UiScale> = ds.data.map { p ->
        p[KEY_UI_SCALE]?.let { runCatching { UiScale.valueOf(it) }.getOrNull() } ?: UiScale.BESAR
    }

    val kaderName: Flow<String?> = ds.data.map { it[KEY_KADER] }

    suspend fun setUiScale(scale: UiScale) {
        ds.edit { it[KEY_UI_SCALE] = scale.name }
    }

    suspend fun setKaderName(name: String) {
        ds.edit { it[KEY_KADER] = name }
    }

    suspend fun isSeeded(): Boolean = ds.data.map { it[KEY_SEEDED] }.first() == "1"

    suspend fun markSeeded() {
        ds.edit { it[KEY_SEEDED] = "1" }
    }

    /** Isi database contoh pertama kali aplikasi dibuka (mode demo mandiri). */
    suspend fun seedIfNeeded() {
        if (isSeeded()) return
        val db = PosyanduDatabase.get(context)
        if (db.targetDao().countSync() > 0) { markSeeded(); return }
        db.familyDao().upsertAll(SampleData.families())
        db.targetDao().upsertAll(SampleData.targets())
        db.visitDao().upsertAll(SampleData.visits())
        db.immunizationDao().upsertAll(SampleData.immunizations())
        markSeeded()
    }
}

/** Akses singleton super-ringan tanpa DI. */
object PrefsHolder {
    @Volatile private var prefs: Prefs? = null
    fun get(context: Context): Prefs =
        prefs ?: synchronized(this) { prefs ?: Prefs(context.applicationContext).also { prefs = it } }
}
