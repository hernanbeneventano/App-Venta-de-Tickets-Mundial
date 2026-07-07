package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
data class DTORegistroCompra(
    val userId: String,
    val partidoId: String,
    val equipo1: String,
    val equipo2: String,
    val flag1: String = "",
    val flag2: String = "",
    val estadio: String = "",
    val cantidad: Int,
    val total: Double,
    val metodoPago: String,
    val fechaCompra: Long,
    val pago_titular: String? = null,
    val pago_tarjeta_mascara: String? = null,
    val pago_cvu_alias: String? = null
)
