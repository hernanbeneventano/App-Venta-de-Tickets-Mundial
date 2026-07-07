package com.example.myapplication28_5_26.services

import com.example.myapplication28_5_26.models.DTOHistorialCompra
import com.example.myapplication28_5_26.models.DTORegistroCompra
import com.example.myapplication28_5_26.models.DTOPartidosDetalle
import com.example.myapplication28_5_26.models.DTOPartidosLista
import com.example.myapplication28_5_26.models.DTOQrAuthRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MundialApiService {

    @GET("api/partidos")
    suspend fun getPartidos(
        @Query("page") page: Int? = null,
        @Query("pageSize") pageSize: Int? = null,
        @Query("grupo") grupo: String? = null,
        @Query("equipo") equipo: String? = null
    ): List<DTOPartidosLista>

    @GET("api/partidos/{id}")
    suspend fun getPartidoById(@Path("id") id: String): DTOPartidosDetalle

    @POST("api/compras")
    suspend fun registrarCompra(@Body compra: DTORegistroCompra): Response<ResponseBody>

    @GET("api/compras/usuario/{userId}")
    suspend fun getComprasByUserId(@Path("userId") userId: String): List<DTOHistorialCompra>

    @DELETE("api/compras/{id}")
    suspend fun eliminarCompra(@Path("id") id: String): Response<ResponseBody>

    @POST("api/auth/qr-authorize")
    suspend fun autorizarQrSession(@Body request: DTOQrAuthRequest): Response<ResponseBody>
}
