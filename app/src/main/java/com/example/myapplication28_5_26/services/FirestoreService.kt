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

    suspend fun registrarCompra(compra: Map<String, Any>) {
        db.collection("compras")
            .add(compra)
            .await()
    }

    suspend fun getComprasByUserId(userId: String): QuerySnapshot {
        return db.collection("compras")
            .whereEqualTo("userId", userId)
            .get()
            .await()
    }
}
