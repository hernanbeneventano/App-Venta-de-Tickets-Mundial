package com.example.myapplication28_5_26.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapplication28_5_26.services.AuthService
import com.example.myapplication28_5_26.services.RetrofitClient
import com.example.myapplication28_5_26.repository.AuthRepository
import com.example.myapplication28_5_26.repository.MundialRepository
import com.example.myapplication28_5_26.ui.navigation.AppNavigation
import com.example.myapplication28_5_26.ui.theme.MyApplication28526Theme
import com.example.myapplication28_5_26.viewmodels.AuthViewModel
import com.example.myapplication28_5_26.viewmodels.MundialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Usamos la nueva API .NET 10 via Retrofit
        val apiService = RetrofitClient.instance
        val repository = MundialRepository(apiService)

        // AuthService sigue usando Firebase Auth directamente (Recomendado para Mobile)
        val authService = AuthService()
        val authRepository = AuthRepository(authService)
        
        val viewModel = MundialViewModel(repository)
        val authViewModel = AuthViewModel(authRepository)

        enableEdgeToEdge()
        setContent {
            MyApplication28526Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavigation(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
