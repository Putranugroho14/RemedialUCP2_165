package com.example.ucp2.view.route

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ucp2.view.*

@Composable
fun PerpustakaanNavGraph(
    navController: NavHostController = rememberNavController(),
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "main_menu",
        modifier = modifier
    ) {
        composable("main_menu") {
            MainMenuScreen(
                onKategoriClick = { navController.navigate(DestinasiHomeKategori.route) },
                onBukuClick = { navController.navigate(DestinasiHomeBuku.route) }
            )
        }


        composable(DestinasiHomeKategori.route) {
            KategoriHomeScreen(
                navigateBack = { navController.popBackStack() },
                navigateToEntry = { navController.navigate(DestinasiEntryKategori.route) },
                onDetailClick = { id -> navController.navigate("${DestinasiDetailKategori.route}/$id") }
            )
        }
        composable(DestinasiEntryKategori.route) {
            KategoriEntryScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = DestinasiDetailKategori.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailKategori.kategoriId) { type = NavType.IntType })
        ) {
            KategoriDetailScreen(
                navigateBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("${DestinasiUpdateKategori.route}/$id") }
            )
        }
        composable(
            route = DestinasiUpdateKategori.routeWithArgs,
            arguments = listOf(navArgument(DestinasiUpdateKategori.kategoriId) { type = NavType.IntType })
        ) {
            KategoriUpdateScreen(navigateBack = { navController.popBackStack() })
        }


        composable(DestinasiHomeBuku.route) {
            BukuHomeScreen(
                navigateBack = { navController.popBackStack() },
                navigateToEntry = { navController.navigate(DestinasiEntryBuku.route) },
                onDetailClick = { id -> navController.navigate("${DestinasiDetailBuku.route}/$id") }
            )
        }
        composable(DestinasiEntryBuku.route) {
            BukuEntryScreen(navigateBack = { navController.popBackStack() })
        }
        composable(
            route = DestinasiDetailBuku.routeWithArgs,
            arguments = listOf(navArgument(DestinasiDetailBuku.bukuId) { type = NavType.IntType })
        ) {
            BukuDetailScreen(
                navigateBack = { navController.popBackStack() },
                onEdit = { id -> navController.navigate("${DestinasiUpdateBuku.route}/$id") }
            )
        }
        composable(
            route = DestinasiUpdateBuku.routeWithArgs,
            arguments = listOf(navArgument(DestinasiUpdateBuku.bukuId) { type = NavType.IntType })
        ) {
            BukuUpdateScreen(navigateBack = { navController.popBackStack() })
        }
    }
}

@Composable
fun MainMenuScreen(
    onKategoriClick: () -> Unit,
    onBukuClick: () -> Unit
) {
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = onKategoriClick, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) {
                Text("Manajemen Kategori")
            }
            Button(onClick = onBukuClick, modifier = Modifier.fillMaxWidth(0.6f).padding(8.dp)) {
                Text("Manajemen Buku")
            }
        }
    }
}
