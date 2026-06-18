package com.example.myapplication28_5_26.services

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult

class AuthService {
    private val auth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

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
