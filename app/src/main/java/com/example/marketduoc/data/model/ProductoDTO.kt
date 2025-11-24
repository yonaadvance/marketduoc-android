package com.example.marketduoc.data.model

data class ProductoDTO(
    val nombre: String,
    val contenido: String, // Aquí enviaremos la descripción
    val precio: Int,       // Tu backend usa Integer, así que usamos Int
    val emailUsuario: String,
    val imagen: String     // Aquí irá la foto en código Base64
)
