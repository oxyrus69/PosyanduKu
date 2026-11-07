package com.posyanduku.app

import android.app.Application
import androidx.datastore.preferences.preferencesDataStore
import android.content.Context
import com.posyanduku.app.data.seed.PrefsHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private val Context.appDataStore by preferencesDataStore(name = "posyanduku_prefs")

class PosyanduApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        CoroutineScope(Dispatchers.IO).launch {
            PrefsHolder.get(this@PosyanduApp).seedIfNeeded()
        }
    }

    companion object {
        lateinit var instance: PosyanduApp
            private set

        fun dataStoreOf(context: Context) = context.appDataStore
    }
}
