package com.example.marketduoc.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.marketduoc.data.model.Articulo
import kotlinx.coroutines.flow.Flow

@Dao
interface ArticuloDao {
    @Query("SELECT * FROM articulos ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<Articulo>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(articulo: Articulo)

    // --- NUEVAS FUNCIONES NECESARIAS ---

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarVarios(articulos: List<Articulo>)

    @Query("DELETE FROM articulos")
    suspend fun borrarTodo()

    @Delete
    suspend fun eliminar(articulo: Articulo)
}