package com.example.myapplication28_5_26.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication28_5_26.models.DTOCompra
import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.repository.MundialRepository
import kotlinx.coroutines.launch

class MundialViewModel(private val repository: MundialRepository) : ViewModel() {

    val partidoLista = mutableStateListOf<DTOPartidosLista>()

    val historialCompras = mutableStateListOf<DTOHistorialCompra>()

    var partidoSeleccionado: DTOPartidosDetalle? by mutableStateOf(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    init {
        getPartidos()
    }

    fun comprarTicket(
        partido: DTOPartidosDetalle,
        userId: String,
        cantidad: Int,
        metodoPago: String,
        detallePago: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            isLoading = true
            try {
                val precioUnitario = partido.precio.toDoubleOrNull() ?: 0.0
                val nuevaCompra = DTOCompra(
                    userId = userId,
                    partidoId = partido.id,
                    equipo1 = partido.equipo1,
                    equipo2 = partido.equipo2,
                    cantidad = cantidad,
                    total = precioUnitario * cantidad,
                    metodoPago = metodoPago,
                    detallePago = detallePago
                )
                val exito = repository.guardarCompra(nuevaCompra)
                onResult(exito)
            } catch (e: Exception) {
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }

    fun getPartidos() {
        viewModelScope.launch {
            isLoading = true
            try {
                val nuevosPartidos = repository.fetchPartidosLista()
                partidoLista.clear()
                partidoLista.addAll(nuevosPartidos)
            } catch (e: Exception) {
                android.util.Log.e("MundialViewModel", "Error al obtener partidos: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun getPartidoById(id: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                partidoSeleccionado = repository.fetchPartidoDetalle(id)
            } catch (e: Exception) {
                android.util.Log.e("MundialViewModel", "Error al obtener partido con ID $id: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchHistorial(userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val compras = repository.getHistorialCompras(userId)
                historialCompras.clear()
                historialCompras.addAll(compras)
            } catch (e: Exception) {
                android.util.Log.e("MundialViewModel", "Error al obtener historial: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }
}
