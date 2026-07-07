package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOHistorialCompra(
    val id: String,
    val equipo1: String,
    val equipo2: String,
    val flag1: String = "",
    val flag2: String = "",
    val estadio: String = "",
    val cantidad: Int,
    val total: Double,
    val fechaCompra: Long,
    val metodoPago: String
)
