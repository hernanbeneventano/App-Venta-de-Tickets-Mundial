package com.example.myapplication28_5_26.repository

import android.util.Log
import com.example.myapplication28_5_26.models.DTOCompra
import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.services.FirestoreService

class MundialRepository(private val firestoreService: FirestoreService) {

    /**
     * Guarda la compra de tickets en Firestore con validaciones de seguridad.
     * - Nunca almacena números de tarjeta completos, solo últimos 4 dígitos
     * - Valida caracteres peligrosos mediante Regex
     * - Separa información de pago para mayor claridad en BD
     */
    suspend fun guardarCompra(compra: DTOCompra): Boolean {
        return try {
            // Estructura base de la compra
            val compraMap = mutableMapOf(
                "userId" to compra.userId,
                "partidoId" to compra.partidoId,
                "equipo1" to compra.equipo1,
                "equipo2" to compra.equipo2,
                "cantidad" to compra.cantidad,
                "total" to compra.total,
                "metodoPago" to compra.metodoPago,
                "fechaCompra" to compra.fechaCompra
            )

            // Procesamos y validamos los datos de pago según el método seleccionado
            if (compra.metodoPago == "Tarjeta de Crédito") {
                val partes = compra.detallePago.split("|")
                val titular = partes.getOrNull(1)?.replace("Titular:", "")?.trim() ?: ""
                val nroCompleto = partes.getOrNull(0)?.replace("Tarjeta:", "")?.trim() ?: ""

                // VALIDACIÓN CRÍTICA: Solo letras, espacios y acentos para titular
                // Solo dígitos para número de tarjeta (previene inyección de caracteres)
                val regexTitular = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
                val regexTarjeta = Regex("^[0-9]+$")

                if (!titular.matches(regexTitular) || !nroCompleto.matches(regexTarjeta)) {
                    Log.w("MundialRepository", "Compra rechazada: caracteres no permitidos en datos de tarjeta")
                    return false
                }

                // Guardamos titular completo pero tarjeta enmascarada por seguridad
                compraMap["pago_titular"] = titular
                compraMap["pago_tarjeta_mascara"] = "**** **** **** ${nroCompleto.takeLast(4)}"
            } else {
                // Procesamiento para Billetera Virtual
                val cvuAlias = compra.detallePago.replace("Billetera (CVU/Alias):", "").trim()

                // VALIDACIÓN: CVU/Alias solo alfanuméricos y puntos (formatos estándar)
                val regexCvuAlias = Regex("^[a-zA-Z0-9.]+$")

                if (!cvuAlias.matches(regexCvuAlias)) {
                    Log.w("MundialRepository", "Compra rechazada: caracteres no permitidos en CVU/Alias")
                    return false
                }

                compraMap["pago_cvu_alias"] = cvuAlias
            }

            // Registra en Firestore y devuelve true si fue exitoso
            firestoreService.registrarCompra(compraMap)
            true
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error al guardar compra: ${e.message}")
            false
        }
    }

    suspend fun fetchPartidosLista(): List<DTOPartidosLista> {
        return try {
            val snapshot = firestoreService.getPartidos()
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
            Log.e("MundialRepository", "Error buscando partidos: ${e.message}", e)
            emptyList()
        }
    }

    suspend fun fetchPartidoDetalle(id: String): DTOPartidosDetalle? {
        return try {
            val doc = firestoreService.getPartidoById(id)
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

    /**
     * Recupera el historial de compras de un usuario específico.
     */
    suspend fun getHistorialCompras(userId: String): List<DTOHistorialCompra> {
        return try {
            val snapshot = firestoreService.getComprasByUserId(userId)
            snapshot.documents.map { doc ->
                DTOHistorialCompra(
                    id = doc.id,
                    equipo1 = doc.getString("equipo1") ?: "N/A",
                    equipo2 = doc.getString("equipo2") ?: "N/A",
                    cantidad = doc.getLong("cantidad")?.toInt() ?: 0,
                    total = doc.getDouble("total") ?: 0.0,
                    fechaCompra = doc.getLong("fechaCompra") ?: 0L,
                    metodoPago = doc.getString("metodoPago") ?: "Desconocido"
                )
            }
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error al obtener historial: ${e.message}")
            emptyList()
        }
    }
}
