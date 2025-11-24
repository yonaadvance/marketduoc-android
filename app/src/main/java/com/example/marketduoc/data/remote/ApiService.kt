package com.example.marketduoc.data.remote

import com.example.marketduoc.data.model.ProductoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    // Esto le dice a Retrofit: "Haz una petición POST a la ruta /productos"
    // Enviando el "sobre" ProductoDTO que acabamos de crear
    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<Any>
}