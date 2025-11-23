package com.example.marketduoc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketduoc.data.model.Articulo
import kotlinx.coroutines.flow.Flow

// Dao objeto de acceso a datos
@Dao
interface ArticuloDao { //reglas

    @Insert(onConflict = OnConflictStrategy.REPLACE) // repite id, remplazo
    suspend fun insertarArticulo(articulo: Articulo) //se crea corru

    @Query("SELECT * FROM articulos ORDER BY id DESC")
    fun obtenerTodosLosArticulos(): Flow<List<Articulo>> //persi avanz

    // aún no hago delete y update :(
}