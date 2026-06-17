package com.example.myapplication28_5_26.models

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Login : Screen()

    @Serializable
    data object Register : Screen()

    @Serializable
    data object PartidosLista : Screen()
    
    @Serializable
    data class PartidosDetalle(val id: String) : Screen()
}
