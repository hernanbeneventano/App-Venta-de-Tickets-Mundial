package com.example.myapplication28_5_26.repository

import android.util.Log
import com.example.myapplication28_5_26.models.DTOCompra
import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTORegistroCompra
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.services.MundialApiService
import retrofit2.Response

class MundialRepository(private val apiService: MundialApiService) {

    /**
     * Guarda la compra de tickets a través de la API .NET 10.
     */
    suspend fun guardarCompra(compra: DTOCompra): Boolean {
        return try {
            var pagoTitular: String? = null
            var pagoTarjetaMascara: String? = null
            var pagoCvuAlias: String? = null

            if (compra.metodoPago == "Tarjeta de Crédito") {
                // Dividimos y aplicamos trim() a cada parte para eliminar espacios invisibles
                val partes = compra.detallePago.split("|").map { it.trim() }
                
                val nroCompleto = partes.getOrNull(0)?.replace("Tarjeta:", "")?.trim() ?: ""
                val titular = partes.getOrNull(1)?.replace("Titular:", "")?.trim() ?: ""

                val regexTitular = Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$")
                val regexTarjeta = Regex("^[0-9]+$")

                if (!titular.matches(regexTitular) || !nroCompleto.matches(regexTarjeta)) {
                    Log.w("MundialRepository", "Validación fallida - Titular: '$titular', Tarjeta: '$nroCompleto'")
                    return false
                }

                pagoTitular = titular
                pagoTarjetaMascara = "**** **** **** ${nroCompleto.takeLast(4)}"
            } else {
                val cvuAlias = compra.detallePago
                    .replace("Billetera:", "")
                    .replace("CVU:", "")
                    .trim()

                val regexCvuAlias = Regex("^[a-zA-Z0-9.]+$")

                if (!cvuAlias.matches(regexCvuAlias)) {
                    Log.w("MundialRepository", "Validación fallida - CVU/Alias: '$cvuAlias'")
                    return false
                }
                pagoCvuAlias = cvuAlias
            }

            val registro = DTORegistroCompra(
                userId = compra.userId,
                partidoId = compra.partidoId,
                equipo1 = compra.equipo1,
                equipo2 = compra.equipo2,
                flag1 = compra.flag1,
                flag2 = compra.flag2,
                estadio = compra.estadio,
                cantidad = compra.cantidad,
                total = compra.total,
                metodoPago = compra.metodoPago,
                fechaCompra = compra.fechaCompra,
                pago_titular = pagoTitular,
                pago_tarjeta_mascara = pagoTarjetaMascara,
                pago_cvu_alias = pagoCvuAlias
            )

            val response = apiService.registrarCompra(registro)
            if (!response.isSuccessful) {
                val errorMsg = response.errorBody()?.string()
                Log.e("MundialRepository", "Error del servidor (Código ${response.code()}): $errorMsg")
                return false
            }
            true
        } catch (e: Exception) {
            Log.e("MundialRepository", "Excepción al guardar compra: ${e.message}", e)
            false
        }
    }

    suspend fun fetchPartidosLista(page: Int? = null, pageSize: Int? = null, grupo: String? = null, equipo: String? = null): List<DTOPartidosLista> {
        return try {
            apiService.getPartidos(page, pageSize, grupo, equipo)
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error buscando partidos via API: ${e.message}")
            emptyList()
        }
    }

    suspend fun fetchPartidoDetalle(id: String): DTOPartidosDetalle? {
        return try {
            apiService.getPartidoById(id)
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error buscando detalle via API: ${e.message}")
            null
        }
    }

    suspend fun getHistorialCompras(userId: String): List<DTOHistorialCompra> {
        return try {
            apiService.getComprasByUserId(userId)
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error al obtener historial via API: ${e.message}")
            emptyList()
        }
    }

    suspend fun eliminarCompra(id: String): Boolean {
        return try {
            val response = apiService.eliminarCompra(id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("MundialRepository", "Error al eliminar compra via API: ${e.message}")
            false
        }
    }
}
