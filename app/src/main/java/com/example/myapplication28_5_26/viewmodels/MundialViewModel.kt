package com.example.myapplication28_5_26.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.repository.MundialRepository
import kotlinx.coroutines.launch

class MundialViewModel(private val repository: MundialRepository) : ViewModel() {

    // Lista de partidos para la pantalla principal
    val partidoLista = mutableStateListOf<DTOPartidosLista>()

    // Partido individual para la pantalla de detalle
    var partidoSeleccionado: DTOPartidosDetalle? by mutableStateOf(null)
        private set

    // Estado de carga
    var isLoading by mutableStateOf(false)
        private set

    init {
        getPartidos()
    }

    fun getPartidos() {
        viewModelScope.launch {
            isLoading = true
            try {
                val nuevosPartidos = repository.fetchPartidosLista()
                partidoLista.clear()
                partidoLista.addAll(nuevosPartidos)
            } catch (e: Exception) {
                // Manejar error
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
                // Manejar error
            } finally {
                isLoading = false
            }
        }
    }
}
