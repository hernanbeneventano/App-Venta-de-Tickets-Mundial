package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOPartidosLista(
    val id: String = "",
    val equipo1: String,
    val equipo2: String,
    val grupo: String,
    val fecha: String,
    val flag1: String,
    val flag2: String
)
