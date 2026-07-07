package com.example.myapplication28_5_26.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication28_5_26.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    var user by mutableStateOf(repository.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun register(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (!validateInput(email, pass)) return
        
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.register(email, pass)
            isLoading = false
            
            result.onSuccess {
                user = it
                onResult(true)
            }.onFailure {
                errorMessage = it.message
                onResult(false)
            }
        }
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (!validateInput(email, pass)) return

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            val result = repository.login(email, pass)
            isLoading = false
            
            result.onSuccess {
                user = it
                onResult(true)
            }.onFailure {
                errorMessage = it.message
                onResult(false)
            }
        }
    }

    fun logout() {
        repository.logout()
        user = null
    }

    private fun validateInput(email: String, pass: String): Boolean {
        // Validar que campos no estén vacíos
        if (email.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor, completa todos los campos"
            return false
        }
        // Validar formato de email usando regex de Android
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "El formato del correo no es válido"
            return false
        }
        // Validar longitud mínima de contraseña (estándar de seguridad)
        if (pass.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }
}
