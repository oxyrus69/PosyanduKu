package com.posyanduku.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.posyanduku.app.feature.detail.DetailScreen
import com.posyanduku.app.feature.detail.DetailViewModel
import com.posyanduku.app.feature.home.HomeScreen
import com.posyanduku.app.feature.home.HomeViewModel
import com.posyanduku.app.feature.list.ListScreen
import com.posyanduku.app.feature.list.ListViewModel
import com.posyanduku.app.feature.register.RegisterScreen
import com.posyanduku.app.feature.register.RegisterViewModel
import com.posyanduku.app.feature.scan.ScanScreen
import com.posyanduku.app.feature.sdidtk.SdidtkScreen
import com.posyanduku.app.feature.sdidtk.SdidtkViewModel
import com.posyanduku.app.feature.settings.SettingsScreen
import com.posyanduku.app.feature.settings.SettingsViewModel
import com.posyanduku.app.feature.visit.VisitScreen
import com.posyanduku.app.feature.visit.VisitViewModel

object Routes {
    const val HOME = "home"
    const val LIST = "list"
    const val SCAN = "scan"
    const val SETTINGS = "settings"
    const val REGISTER = "register"
    const val DETAIL = "detail/{id}"
    const val VISIT = "visit/{id}"
    const val SDIDTK = "sdidtk/{id}"
    fun detail(id: String) = "detail/$id"
    fun visit(id: String) = "visit/$id"
    fun sdidtk(id: String) = "sdidtk/$id"
}

@Composable
fun RootNav(
    homeVm: HomeViewModel, listVm: ListViewModel,
    detailVm: DetailViewModel, registerVm: RegisterViewModel,
    visitVm: VisitViewModel, sdidtkVm: SdidtkViewModel,
    settingsVm: SettingsViewModel,
) {
    val nav = rememberNavController()
    val backStack by nav.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Routes.HOME
    val showBottom = current in listOf(Routes.HOME, Routes.LIST, Routes.SCAN, Routes.SETTINGS)

    Scaffold(
        bottomBar = {
            if (showBottom) {
                NavigationBar(modifier = Modifier.height(96.dp)) {
                    BottomItem("Utama", Icons.Filled.Home, current == Routes.HOME) {
                        nav.navigate(Routes.HOME) { popUpTo(Routes.HOME) { inclusive = true } }
                    }
                    BottomItem("Warga", Icons.Filled.List, current == Routes.LIST) {
                        nav.navigate(Routes.LIST)
                    }
                    BottomItem("Pindai", Icons.Filled.QrCodeScanner, current == Routes.SCAN) {
                        nav.navigate(Routes.SCAN)
                    }
                    BottomItem("Atur", Icons.Filled.Settings, current == Routes.SETTINGS) {
                        nav.navigate(Routes.SETTINGS)
                    }
                }
            }
        },
    ) { pad ->
        Box(Modifier.fillMaxSize().padding(pad)) {
            NavHost(navController = nav, startDestination = Routes.HOME) {
                composable(Routes.HOME) {
                    HomeScreen(
                        vm = homeVm,
                        onOpenTarget = { nav.navigate(Routes.detail(it)) },
                        onVisit = { nav.navigate(Routes.visit(it)) },
                        onSeeAll = { nav.navigate(Routes.LIST) },
                        onRegister = { nav.navigate(Routes.REGISTER) },
                    )
                }
                composable(Routes.LIST) {
                    ListScreen(
                        vm = listVm,
                        onOpenTarget = { nav.navigate(Routes.detail(it)) },
                        onVisit = { nav.navigate(Routes.visit(it)) },
                        onRegister = { nav.navigate(Routes.REGISTER) },
                    )
                }
                composable(Routes.SCAN) { ScanScreen() }
                composable(Routes.SETTINGS) { SettingsScreen(vm = settingsVm) }
                composable(Routes.REGISTER) {
                    RegisterScreen(vm = registerVm) { nav.popBackStack() }
                }
                composable(Routes.DETAIL, arguments = listOf(navArgument("id") { type = NavType.StringType })) { e ->
                    val id = e.arguments?.getString("id") ?: return@composable
                    DetailScreen(
                        vm = detailVm, targetId = id,
                        onBack = { nav.popBackStack() },
                        onVisit = { nav.navigate(Routes.visit(it)) },
                        onSdidtk = { nav.navigate(Routes.sdidtk(it)) },
                    )
                }
                composable(Routes.VISIT, arguments = listOf(navArgument("id") { type = NavType.StringType })) { e ->
                    val id = e.arguments?.getString("id") ?: return@composable
                    VisitScreen(vm = visitVm, targetId = id) { nav.popBackStack() }
                }
                composable(Routes.SDIDTK, arguments = listOf(navArgument("id") { type = NavType.StringType })) { e ->
                    val id = e.arguments?.getString("id") ?: return@composable
                    SdidtkScreen(vm = sdidtkVm, targetId = id) { nav.popBackStack() }
                }
            }
        }
    }
}


