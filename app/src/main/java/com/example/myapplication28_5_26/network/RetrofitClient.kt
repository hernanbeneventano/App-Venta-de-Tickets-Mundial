package com.example.myapplication28_5_26.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit

object RetrofitClient {
    private val json = Json { ignoreUnknownKeys = true }
    
    val api: MundialApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/Iatapi/PracticaTPI/main/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
            .create(MundialApiService::class.java)
    }
}
