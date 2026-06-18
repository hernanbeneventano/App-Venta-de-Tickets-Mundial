package com.example.myapplication28_5_26.services

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.tasks.await

class FirestoreService {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getPartidos(): QuerySnapshot {
        return db.collection("partidos")
            .orderBy("fecha")
            .get()
            .await()
    }

    suspend fun getPartidoById(id: String): DocumentSnapshot {
        return db.collection("partidos")
            .document(id)
            .get()
            .await()
    }
}
