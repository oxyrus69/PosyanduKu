package com.posyanduku.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalDensity
import androidx.lifecycle.viewmodel.compose.viewModel
import com.posyanduku.app.data.seed.PrefsHolder
import com.posyanduku.app.ui.theme.PosyanduKuTheme
import com.posyanduku.app.ui.theme.UiScale
import com.posyanduku.app.feature.home.HomeViewModel
import com.posyanduku.app.feature.list.ListViewModel
import com.posyanduku.app.feature.register.RegisterViewModel
import com.posyanduku.app.feature.settings.SettingsViewModel
import com.posyanduku.app.feature.sdidtk.SdidtkViewModel
import com.posyanduku.app.feature.detail.DetailViewModel
import com.posyanduku.app.feature.visit.VisitViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val prefs = remember { PrefsHolder.get(this) }
            val uiScale by prefs.uiScale.collectAsState(initial = UiScale.BESAR)

            // Skala manual UI: kita perbesar fontScale agar seluruh ukuran teks
            // ikut membesar. Nilai ini sudah diterapkan satu kali lewat
            // posyanduTypography(scale) di theme, jadi di sini TIDAK digandakan
            // lagi (kalau digandakan, 1.5x jadi 2.25x dan layout mulai terpotong).
            val baseDensity = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides baseDensity) {
                PosyanduKuTheme(uiScale = uiScale) {
                    RootNav(
                        homeVm = viewModel { HomeViewModel(application) },
                        listVm = viewModel { ListViewModel(application) },
                        detailVm = viewModel { DetailViewModel(application) },
                        registerVm = viewModel { RegisterViewModel(application) },
                        visitVm = viewModel { VisitViewModel(application) },
                        sdidtkVm = viewModel { SdidtkViewModel(application) },
                        settingsVm = viewModel { SettingsViewModel(application) },
                    )
                }
            }
        }
    }
}
