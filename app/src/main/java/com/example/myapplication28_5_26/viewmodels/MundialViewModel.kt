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

    private var currentPage = 1
    private val pageSize = 6
    var isEndReached by mutableStateOf(false)
        private set

    var searchQuery by mutableStateOf("")
    var selectedGrupo by mutableStateOf("TODOS")

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
                    flag1 = partido.flag1,
                    flag2 = partido.flag2,
                    estadio = partido.estadio,
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
        if (isLoading || isEndReached) return

        viewModelScope.launch {
            isLoading = true
            try {
                val nuevosPartidos = repository.fetchPartidosLista(
                    page = currentPage,
                    pageSize = pageSize,
                    grupo = if (selectedGrupo == "TODOS") null else selectedGrupo,
                    equipo = if (searchQuery.isBlank()) null else searchQuery
                )
                if (nuevosPartidos.isEmpty()) {
                    isEndReached = true
                } else {
                    partidoLista.addAll(nuevosPartidos)
                    currentPage++
                    if (nuevosPartidos.size < pageSize) {
                        isEndReached = true
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MundialViewModel", "Error al obtener partidos: ${e.message}", e)
            } finally {
                isLoading = false
            }
        }
    }

    fun refreshPartidos() {
        currentPage = 1
        isEndReached = false
        partidoLista.clear()
        getPartidos()
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

    fun cancelarCompra(id: String, userId: String) {
        viewModelScope.launch {
            isLoading = true
            try {
                val exito = repository.eliminarCompra(id)
                if (exito) {
                    fetchHistorial(userId)
                }
            } catch (e: Exception) {
                android.util.Log.e("MundialViewModel", "Error al cancelar compra: ${e.message}")
            } finally {
                isLoading = false
            }
        }
    }

    fun autorizarSesionWeb(sessionId: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            isLoading = true
            try {
                val exito = repository.autorizarQr(sessionId)
                onResult(exito)
            } catch (e: Exception) {
                onResult(false)
            } finally {
                isLoading = false
            }
        }
    }
}
