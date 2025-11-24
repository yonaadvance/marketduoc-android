package com.example.marketduoc.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.model.Articulo // Modelo local (Room)
import com.example.marketduoc.data.model.ProductoDTO // Modelo para enviar (API)
import com.example.marketduoc.data.remote.RetrofitClient
import com.example.marketduoc.utils.ImageUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticuloViewModel(private val dao: ArticuloDao) : ViewModel() {

    // Mantenemos la lectura local por ahora para que la app no se vea vacía si usas Room
    val todosLosArticulos: StateFlow<List<Articulo>> = dao.obtenerTodosLosArticulos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = emptyList()
        )

    // Función modificada: Ahora pide 'context' para poder procesar la imagen
    fun insertarArticulo(context: Context, titulo: String, descripcion: String, precio: String, fotoUri: Uri) {
        viewModelScope.launch {
            try {
                Log.d("API", "Preparando envío...")

                // 1. Convertir la imagen a Base64
                val imagenBase64 = ImageUtils.uriToBase64(context, fotoUri)

                // 2. Crear el objeto para enviar a la API
                val productoDTO = ProductoDTO(
                    nombre = titulo,
                    contenido = descripcion,
                    precio = precio.toIntOrNull() ?: 0,
                    emailUsuario = "usuario@duocuc.cl", // Email fijo por ahora (o sácalo del Login si quieres)
                    imagen = imagenBase64
                )

                // 3. ¡ENVIAR A TU PC! 🚀
                Log.d("API", "Enviando a: ${RetrofitClient.instance.baseUrl()}")
                val respuesta = RetrofitClient.apiService.crearProducto(productoDTO)

                if (respuesta.isSuccessful) {
                    Log.d("API", "¡Éxito! Producto creado: ${respuesta.body()}")

                    // Opcional: Guardar también en Room para verlo offline (Estrategia Híbrida)
                    val nuevoArticuloLocal = Articulo(
                        titulo = titulo,
                        descripcion = descripcion,
                        precio = precio.toDoubleOrNull() ?: 0.0,
                        fotoUri = fotoUri.toString()
                    )
                    dao.insertarArticulo(nuevoArticuloLocal)

                } else {
                    Log.e("API", "Error en el servidor: ${respuesta.code()} - ${respuesta.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                Log.e("API", "Error de conexión: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}