package com.example.myapplication28_5_26.repository

import com.example.myapplication28_5_26.services.AuthService
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val authService: AuthService) {

    val currentUser: FirebaseUser?
        get() = authService.currentUser

    suspend fun login(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val result = authService.signIn(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    suspend fun register(email: String, pass: String): Result<FirebaseUser?> {
        return try {
            val result = authService.signUp(email, pass).await()
            Result.success(result.user)
        } catch (e: Exception) {
            Result.failure(Exception(mapFirebaseError(e.message)))
        }
    }

    fun logout() {
        authService.signOut()
    }

    private fun mapFirebaseError(message: String?): String {
        // Mapea errores específicos de Firebase a mensajes amigables en español
        // Permite mejor UX al mostrar errores claros al usuario
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
