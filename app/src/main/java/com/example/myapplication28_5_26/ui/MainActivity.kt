package com.example.myapplication28_5_26.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.myapplication28_5_26.network.RetrofitClient
import com.example.myapplication28_5_26.repository.MundialRepository
import com.example.myapplication28_5_26.ui.navigation.AppNavigation
import com.example.myapplication28_5_26.ui.theme.MyApplication28526Theme
import com.example.myapplication28_5_26.viewmodels.MundialViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicialización manual de las dependencias
        val apiService = RetrofitClient.api
        val repository = MundialRepository(apiService)
        val viewModel = MundialViewModel(repository)

        enableEdgeToEdge()
        setContent {
            MyApplication28526Theme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    // Conectamos la navegación principal
                    AppNavigation(
                        viewModel = viewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
