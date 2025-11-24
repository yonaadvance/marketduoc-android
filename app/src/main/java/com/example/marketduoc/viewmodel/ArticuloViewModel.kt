package com.example.marketduoc.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marketduoc.data.local.dao.ArticuloDao
import com.example.marketduoc.data.model.Articulo
import com.example.marketduoc.data.model.ProductoDTO
import com.example.marketduoc.data.remote.RetrofitClient
import com.example.marketduoc.utils.ImageUtils
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticuloViewModel(private val dao: ArticuloDao) : ViewModel() {

    val todosLosArticulos: StateFlow<List<Articulo>> = dao.obtenerTodosLosArticulos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    init {
        sincronizarDatos()
    }

    fun sincronizarDatos() {
        viewModelScope.launch {
            try {
                Log.d("API", "Sincronizando...")
                val respuesta = RetrofitClient.apiService.obtenerProductos()
                if (respuesta.isSuccessful && respuesta.body() != null) {
                    dao.borrarTodo()
                    respuesta.body()!!.forEach { dto ->
                        // --- AQUÍ ESTÁ EL ARREGLO "BLINDAJE" ---
                        dao.insertarArticulo(Articulo(
                            titulo = dto.nombre,
                            descripcion = dto.contenido,
                            precio = dto.precio.toDouble(),
                            // Si imagen es null, guardamos un texto vacío ""
                            fotoUri = dto.imagen ?: "",
                            // Si email es null, ponemos "Anónimo"
                            emailVendedor = dto.emailUsuario ?: "Anónimo",
                            // Si categoría es null, ponemos 1
                            categoriaId = dto.categoriaId ?: 1L
                        ))
                    }
                    Log.d("API", "Sincronización completa.")
                }
            } catch (e: Exception) {
                Log.e("API", "Error sync: ${e.message}")
            }
        }
    }

    fun insertarArticulo(context: Context, titulo: String, descripcion: String, precio: String, fotoUri: Uri, categoriaId: Long) {
        viewModelScope.launch {
            try {
                val imagenBase64 = ImageUtils.uriToBase64(context, fotoUri)
                val email = FirebaseAuth.getInstance().currentUser?.email ?: "anonimo@duocuc.cl"

                val dto = ProductoDTO(titulo, descripcion, precio.toIntOrNull() ?: 0, email, imagenBase64, categoriaId)

                val respuesta = RetrofitClient.apiService.crearProducto(dto)
                if (respuesta.isSuccessful) {
                    Log.d("API", "Creado.")
                    sincronizarDatos()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}