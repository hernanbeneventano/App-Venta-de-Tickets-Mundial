package com.example.myapplication28_5_26.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.myapplication28_5_26.services.AuthService

class AuthViewModel : ViewModel() {
    private val authService = AuthService()

    var user by mutableStateOf(authService.currentUser)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun register(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (!validateInput(email, pass)) return
        
        isLoading = true
        errorMessage = null
        authService.signUp(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    user = authService.currentUser
                    onResult(true)
                } else {
                    errorMessage = mapFirebaseError(task.exception?.message)
                    onResult(false)
                }
            }
    }

    fun login(email: String, pass: String, onResult: (Boolean) -> Unit) {
        if (!validateInput(email, pass)) return

        isLoading = true
        errorMessage = null
        authService.signIn(email, pass)
            .addOnCompleteListener { task ->
                isLoading = false
                if (task.isSuccessful) {
                    user = authService.currentUser
                    onResult(true)
                } else {
                    errorMessage = mapFirebaseError(task.exception?.message)
                    onResult(false)
                }
            }
    }

    fun logout() {
        authService.signOut()
        user = null
    }

    private fun validateInput(email: String, pass: String): Boolean {
        if (email.isBlank() || pass.isBlank()) {
            errorMessage = "Por favor, completa todos los campos"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            errorMessage = "El formato del correo no es válido"
            return false
        }
        if (pass.length < 6) {
            errorMessage = "La contraseña debe tener al menos 6 caracteres"
            return false
        }
        return true
    }

    private fun mapFirebaseError(message: String?): String {
        return when {
            message == null -> "Ocurrió un error desconocido"
            message.contains("user-not-found") -> "El usuario no está registrado"
            message.contains("wrong-password") -> "La contraseña es incorrecta"
            message.contains("email-already-in-use") -> "Este correo ya está en uso"
            message.contains("network-request-failed") -> "Error de conexión a internet"
            else -> "Error: $message"
        }
    }
}
