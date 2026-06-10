package com.example.myapplication28_5_26.repository

import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.network.MundialApiService

class MundialRepository(private val api: MundialApiService) {
    suspend fun fetchPartidosLista(): List<DTOPartidosLista>{
        return api.getPartidosLista()
    }

    suspend fun fetchPartidosDetalle(): DTOPartidosDetalle{
        return api.getPartidosDetalle()[0]
    }}

