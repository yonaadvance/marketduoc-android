package com.example.marketduoc.data.model

data class ProductoDTO(
    val nombre: String,
    val contenido: String,
    val precio: Int,
    val emailUsuario: String?, // <--- Puede ser nulo
    val imagen: String?,       // <--- Puede ser nulo (EL CULPABLE)
    val categoriaId: Long?     // <--- Puede ser nulo
)