package com.example.myapplication28_5_26.services

import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.models.DTORegistroCompra
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MundialApiService {

    @GET("api/partidos")
    suspend fun getPartidos(): List<DTOPartidosLista>

    @GET("api/partidos/{id}")
    suspend fun getPartidoById(@Path("id") id: String): DTOPartidosDetalle

    @POST("api/compras")
    suspend fun registrarCompra(@Body compra: DTORegistroCompra): Response<ResponseBody>

    @GET("api/compras/usuario/{userId}")
    suspend fun getComprasByUserId(@Path("userId") userId: String): List<DTOHistorialCompra>
}
