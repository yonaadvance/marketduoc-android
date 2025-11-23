package com.example.marketduoc.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "articulos") //creo tabla
data class Articulo(                    //dc almacen dato, auto tostring
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,  //room ponele los id tú

    val titulo: String,
    val descripcion: String,
    val precio: Double,  //no se si double estará bien

    val fotoUri: String //guarda ubi de la foto y la muestra
)

