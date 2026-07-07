package com.example.myapplication28_5_26.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

import kotlinx.coroutines.tasks.await

class AuthService {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun getIdToken(): String? {
        return try {
            auth.currentUser?.getIdToken(false)?.await()?.token
        } catch (e: Exception) {
            android.util.Log.e("AuthService", "Error al obtener token", e)
            null
        }
    }

    fun signIn(email: String, pass: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, pass)
    }

    fun signUp(email: String, pass: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, pass)
    }

    fun signOut() {
        auth.signOut()
    }
}
