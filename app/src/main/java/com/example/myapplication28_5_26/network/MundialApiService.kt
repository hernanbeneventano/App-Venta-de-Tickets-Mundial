package com.example.myapplication28_5_26.network

import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import retrofit2.http.GET

interface MundialApiService {
    
    @GET("PartidoLista")
    suspend fun getPartidosLista(): List<DTOPartidosLista>
    
    @GET("PartidoDetalle")
    suspend fun getPartidosDetalle(): List<DTOPartidosDetalle>
}
