package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOPartidosDetalle(
    val id: String = "",
    val equipo1: String,
    val equipo2: String,
    val grupo: String,
    val fecha: Long,
    val flag1: String,
    val flag2: String,
    val precio: String,
    val estadio: String
)
