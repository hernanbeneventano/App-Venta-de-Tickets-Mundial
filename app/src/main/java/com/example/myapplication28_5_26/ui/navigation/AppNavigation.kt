package com.example.myapplication28_5_26.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication28_5_26.models.Screen
import com.example.myapplication28_5_26.ui.screens.PantallaLista
import com.example.myapplication28_5_26.viewmodels.MundialViewModel

@Composable
fun AppNavigation(viewModel: MundialViewModel, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.PartidosLista,
        modifier = modifier
    ) {
        composable<Screen.PartidosLista> {
            PantallaLista(navController, viewModel)
        }
        composable<Screen.PartidosDetalle> { backStackEntry ->
            val route = backStackEntry.toRoute<Screen.PartidosDetalle>()
            // PantallaDetalle(route.id, viewModel) // Próximamente
        }
    }
}
