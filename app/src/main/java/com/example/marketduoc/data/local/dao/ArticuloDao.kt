package com.example.marketduoc.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.marketduoc.data.model.Articulo
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticuloDao {
    @Query("SELECT * FROM articulos ORDER BY id DESC")
    fun obtenerTodosLosArticulos(): Flow<List<Articulo>>

    @Insert
    suspend fun insertarArticulo(articulo: Articulo)

    @Query("DELETE FROM articulos") // <--- Nuevo
    suspend fun borrarTodo()
}