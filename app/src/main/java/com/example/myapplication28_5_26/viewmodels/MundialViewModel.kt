package com.example.myapplication28_5_26.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.network.RetrofitClient
import kotlinx.coroutines.launch

sealed interface MundialUiState {
    data class Success(val partidos: List<DTOPartidosLista>) : MundialUiState
    data object Error : MundialUiState
    data object Loading : MundialUiState
}

sealed interface DetalleUiState {
    data class Success(val partido: DTOPartidosDetalle) : DetalleUiState
    data object Error : DetalleUiState
    data object Loading : DetalleUiState
}

class MundialViewModel : ViewModel() {
    
    var mundialUiState: MundialUiState by mutableStateOf(MundialUiState.Loading)
        private set

    var detalleUiState: DetalleUiState by mutableStateOf(DetalleUiState.Loading)
        private set

    init {
        getPartidos()
    }

    fun getPartidos() {
        viewModelScope.launch {
            mundialUiState = MundialUiState.Loading
            mundialUiState = try {
                MundialUiState.Success(RetrofitClient.api.getPartidosLista())
            } catch (e: Exception) {
                MundialUiState.Error
            }
        }
    }

    fun getPartidoById(id: Int) {
        viewModelScope.launch {
            detalleUiState = DetalleUiState.Loading
            detalleUiState = try {
                val detalle = RetrofitClient.api.getPartidosDetalle().find { it.id == id }
                if (detalle != null) {
                    DetalleUiState.Success(detalle)
                } else {
                    DetalleUiState.Error
                }
            } catch (e: Exception) {
                DetalleUiState.Error
            }
        }
    }
}
