package com.example.marketduoc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos")
data class Articulo(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, // ID interno del celular
    val idRemoto: Long? = null,                       // <--- ID DE RENDER (Para poder borrar)
    val titulo: String,
    val descripcion: String,
    val precio: Double,
    val fotoUri: String,
    val emailVendedor: String = "",
    val categoriaId: Long = 1
)