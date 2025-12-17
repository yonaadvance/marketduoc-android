package com.example.marketduoc.data.remote

import com.example.marketduoc.data.model.ProductoDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE // <--- Necesario para borrar
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path   // <--- Necesario para decirle qué ID borrar

interface ApiService {
    @POST("productos")
    suspend fun crearProducto(@Body producto: ProductoDTO): Response<Any>

    @GET("productos")
    suspend fun obtenerProductos(): Response<List<ProductoDTO>>

    @DELETE("productos/{id}") // <--- El endpoint para eliminar
    suspend fun eliminarProducto(@Path("id") id: Long): Response<Unit>
}