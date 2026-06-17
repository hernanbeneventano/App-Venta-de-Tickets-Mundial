package com.example.myapplication28_5_26.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.myapplication28_5_26.models.Screen
import com.example.myapplication28_5_26.ui.screens.LoginScreen
import com.example.myapplication28_5_26.ui.screens.PantallaDetalle
import com.example.myapplication28_5_26.ui.screens.PantallaLista
import com.example.myapplication28_5_26.ui.screens.RegisterScreen
import com.example.myapplication28_5_26.viewmodels.AuthViewModel
import com.example.myapplication28_5_26.viewmodels.MundialViewModel

@Composable
fun AppNavigation(
    viewModel: MundialViewModel,
    authViewModel: AuthViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    val startDestination: Any = if (authViewModel.user != null) {
        Screen.PartidosLista
    } else {
        Screen.Login
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.Login> {
            LoginScreen(navController, authViewModel)
        }
        composable<Screen.Register> {
            RegisterScreen(navController, authViewModel)
        }
        composable<Screen.PartidosLista> {
            PantallaLista(navController, viewModel, authViewModel)
        }
        composable<Screen.PartidosDetalle> { backStackEntry ->
            val route: Screen.PartidosDetalle = backStackEntry.toRoute()
            PantallaDetalle(navController, viewModel, route.id)
        }
    }
}
