package com.example.marketduoc.data.remote

import com.example.marketduoc.data.model.ProductoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET // <--- Importar
import retrofit2.http.POST

interface ApiService {
    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<Any>

    @GET("productos") // <--- Nuevo endpoint
    suspend fun obtenerProductos(): Response<List<ProductoDTO>>
}