package com.example.marketduoc.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.model.Articulo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticuloViewModel(private val dao: ArticuloDao) : ViewModel() {

    val todosLosArticulos: StateFlow<List<Articulo>> = dao.obtenerTodosLosArticulos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    fun insertarArticulo(titulo: String, descripcion: String, precio: String, fotoUri: Uri) {
        viewModelScope.launch {
            try {
                val nuevoArticulo = Articulo(
                    titulo = titulo,
                    descripcion = descripcion,
                    precio = precio.toDoubleOrNull() ?: 0.0,
                    fotoUri = fotoUri.toString()
                )
                dao.insertarArticulo(nuevoArticulo)
            } catch (e: Exception) {
                Log.e("ArticuloViewModel", "Error al insertar artículo: ${e.message}")
            }
        }
    }
}