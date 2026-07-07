package com.example.myapplication28_5_26.services

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // URL DEFINITIVA EN RENDER
    private const val BASE_URL = "https://mundialapi-zbsj.onrender.com/"

    private val authInterceptor = Interceptor { chain ->
        val user = FirebaseAuth.getInstance().currentUser
        val requestBuilder = chain.request().newBuilder()

        if (user != null) {
            try {
                // Obtenemos el token de forma síncrona (estamos en un hilo de red de OkHttp)
                val task = user.getIdToken(false)
                val tokenResult = Tasks.await(task)
                val token = tokenResult.token
                if (token != null) {
                    requestBuilder.addHeader("Authorization", "Bearer $token")
                }
            } catch (e: Exception) {
                android.util.Log.e("RetrofitClient", "Error obteniendo el token de Firebase", e)
            }
        }

        chain.proceed(requestBuilder.build())
    }

    // Cliente OkHttp con tiempo de espera extendido para Render Free Tier (spin-up)
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor) // Agregamos el interceptor de seguridad
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val instance: MundialApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MundialApiService::class.java)
    }
}
