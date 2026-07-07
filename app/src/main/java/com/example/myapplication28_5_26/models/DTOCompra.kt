package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTOCompra(
    val userId: String,
    val partidoId: String,
    val equipo1: String,
    val equipo2: String,
    val flag1: String,
    val flag2: String,
    val estadio: String,
    val cantidad: Int,
    val total: Double,
    val metodoPago: String,
    val detallePago: String, // Aquí guardaremos "Visa **** 1234" o "MercadoPago: user@mail.com"
    val fechaCompra: Long = System.currentTimeMillis()
)
