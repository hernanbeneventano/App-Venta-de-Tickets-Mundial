package com.example.myapplication28_5_26.repository

import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MundialRepository {
    private val db = FirebaseFirestore.getInstance()

    suspend fun fetchPartidosLista(): List<DTOPartidosLista> {
        return try {
            val snapshot = db.collection("partidos")
                .orderBy("fecha")
                .get()
                .await()
            snapshot.documents.map { doc ->
                DTOPartidosLista(
                    id = doc.id,
                    equipo1 = doc.getString("equipo1") ?: "",
                    equipo2 = doc.getString("equipo2") ?: "",
                    grupo = doc.getString("grupo") ?: "",
                    fecha = doc.getString("fecha") ?: "",
                    flag1 = doc.getString("flag1") ?: "",
                    flag2 = doc.getString("flag2") ?: ""
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchPartidoDetalle(id: String): DTOPartidosDetalle? {
        return try {
            val doc = db.collection("partidos").document(id).get().await()
            if (doc.exists()) {
                DTOPartidosDetalle(
                    id = doc.id,
                    equipo1 = doc.getString("equipo1") ?: "",
                    equipo2 = doc.getString("equipo2") ?: "",
                    grupo = doc.getString("grupo") ?: "",
                    fecha = doc.getString("fecha") ?: "",
                    flag1 = doc.getString("flag1") ?: "",
                    flag2 = doc.getString("flag2") ?: "",
                    estadio = doc.getString("estadio") ?: "Estadio Mundialista",
                    precio = doc.getString("precio") ?: "100"
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }
}
