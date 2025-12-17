package com.example.marketduoc.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Tu URL de Ngrok (verifica que siga siendo la misma)
    // URL oficial de Render
    private const val BASE_URL = "https://backend-marketduoc.onrender.com/api/v1/"

    // --- CONFIGURACIÓN DE PACIENCIA ---
    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS) // Tiempo para conectar
        .readTimeout(60, TimeUnit.SECONDS)    // Tiempo esperando a que el servidor responda (GET)
        .writeTimeout(60, TimeUnit.SECONDS)   // Tiempo subiendo la foto (POST)
        .build()

    val instance: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <--- Le decimos a Retrofit que use nuestra configuración paciente
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiService: ApiService by lazy {
        instance.create(ApiService::class.java)
    }
}