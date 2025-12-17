package com.example.marketduoc.data.model

data class ProductoDTO(
    val id: Long? = null, // <--- ¡Aquí está la magia! El bolsillo para guardar el ID
    val nombre: String,
    val contenido: String,
    val precio: Int,
    val emailUsuario: String?,
    val imagen: String?,
    val categoriaId: Long?
)